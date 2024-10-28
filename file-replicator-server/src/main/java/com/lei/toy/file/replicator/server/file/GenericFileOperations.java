package com.lei.toy.file.replicator.server.file;

import com.lei.toy.file.replicator.core.command.Command;
import com.lei.toy.file.replicator.core.util.Compressor;
import com.lei.toy.file.replicator.server.properties.FileReplicatorServerProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * lastPosition=文件前4位
 * lastCommandTimestamp=lastPosition后8位
 * </p>
 *
 * @author 伍磊
 */
@Slf4j
public class GenericFileOperations implements FileOperations {

    private final FileReplicatorServerProperties serverProperties;
    private final Compressor compressor;
    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;
    private AtomicInteger writePos;
    // todo 需要测试内存使用、操作性能等
    private TreeMap<Long, Integer> timestamp2PositionMap = new TreeMap<>();
    private ReentrantLock reentrantLock = new ReentrantLock();

    public GenericFileOperations(FileReplicatorServerProperties serverProperties, Compressor compressor) {
        this.serverProperties = serverProperties;
        this.compressor = compressor;
        // todo 启动timestamp2PositionMap清理线程
    }

    @Override
    public void init() throws Exception {
        // 初始化fileDir
        String dirStr = serverProperties.getDir();
        Path dir = Paths.get(dirStr);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String fileName = serverProperties.getFileName();
        String fileExt = serverProperties.getFileExt();
        String dataFileName = dirStr + generateFileName(fileName, fileExt);
        // mmap
        randomAccessFile = new RandomAccessFile(dataFileName, "rw");
        fileChannel = randomAccessFile.getChannel();
        mappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_WRITE,
                0,
                serverProperties.getFileMaxSize()
        );
        // 文件前4位固定存储position
        int position = getLastWritePos();
        writePos.set(position);
    }

    @Override
    public void appendCommand(Command command) {
        ByteBuffer byteBuffer = command.warp();
        byteBuffer.flip();
        int commandSize = byteBuffer.limit();
        int retry = 3;
        while (retry-- > 0) {
            int curPosition = writePos.get();
            int nextPosition = curPosition + commandSize;
            if (writePos.compareAndSet(curPosition, nextPosition)) {
                // todo 如何保证追加数据、更新position、更新timestamp2PositionMap的完整性
                // 目前仅保证单节点
                reentrantLock.lock();
                try {
                    MappedByteBuffer duplicate = mappedByteBuffer.duplicate();
                    duplicate.position(curPosition);
                    duplicate.put(byteBuffer);
                    timestamp2PositionMap.put(command.timestamp(), curPosition);
                    writeLastWritePos(nextPosition);
                    writeLastCommandTimestamp(command.timestamp());
                    // todo 同步/异步刷盘??
                    // todo 上述代码出现异常怎么处理？？position需要回滚吗？
                } finally {
                    reentrantLock.unlock();
                }
                return;
            }
        }
        throw new RuntimeException("append error!");
    }


    @Override
    public byte[] scan(Long timestamp) throws IOException {
        int curPos = writePos.get();
        if (curPos <= skipPos()) {
            return new byte[0];
        }
        int readPos;
        if (null == timestamp) {
            readPos = skipPos();
        } else {
            Long searchKey = timestamp2PositionMap.ceilingKey(timestamp);
            // todo 需要考虑readPos不存在的情况
            readPos = timestamp2PositionMap.get(searchKey);
        }
        MappedByteBuffer duplicate = mappedByteBuffer.duplicate();
        duplicate.limit(curPos);
        duplicate.position(readPos);
        byte[] bytes = new byte[curPos - readPos];
        duplicate.get(bytes);
        // compressor
        return compressor.compress(bytes);
    }

    @Override
    public void close() throws Exception {
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        if (fileChannel != null) {
            fileChannel.close();
        }
    }

    private String generateFileName(String fileName, String fileExt) {
        return fileName + fileExt;
    }

    public int getLastWritePos() {
        return mappedByteBuffer.getInt(0);
    }

    public void writeLastWritePos(int pos) {
        mappedByteBuffer.putInt(0, pos);
    }

    public long getLastCommandTimestamp() {
        return mappedByteBuffer.getLong(4);
    }

    public void writeLastCommandTimestamp(long timestamp) {
        mappedByteBuffer.putLong(4, timestamp);
    }

    public int skipPos() {
        return 4 + 8;
    }
}
