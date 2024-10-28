package com.lei.toy.file.replicator.core.command;

import java.nio.ByteBuffer;
import java.time.Instant;

public record Command(long timestamp,
                      byte type,
                      byte[] data) {

    public Command(CommandTypeEnum type, byte[] data) {
        this(Instant.now().toEpochMilli(), type.getCode(), data);
    }

    public ByteBuffer warp() {
        int bufferSize = getBufferSize(data.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        byteBuffer.putLong(this.timestamp);
        byteBuffer.put(this.type);
        byteBuffer.putInt(this.data.length);
        byteBuffer.put(this.data);
        return byteBuffer;
    }

    public static Command parse(byte[] bytes) {
        if (bytes == null || bytes.length < getMinLength()) {
            throw new IllegalArgumentException("bytes error!");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long timestamp = byteBuffer.getLong();
        byte type = byteBuffer.get();
        int dataLength = byteBuffer.getInt();
        if ((byteBuffer.limit() - byteBuffer.position()) != dataLength) {
            throw new IllegalArgumentException("bytes error!");
        }
        byte[] data = new byte[dataLength];
        byteBuffer.get(data);
        return new Command(timestamp, type, data);
    }

    private int getBufferSize(int dataLength) {
        // 时间戳(8) + 类型(1) + dataLength(4) + data
        return getMinLength() + dataLength;
    }

    public static int getMinLength() {
        // 时间戳(8) + 类型(1) + dataLength(4)
        return 8 + 1 + 4;
    }

}
