package com.lei.toy.file.replicator.server.file;

import com.lei.toy.file.replicator.core.command.Command;

import java.io.IOException;

/**
 * <p>
 * FileOperations   <br/>
 * 目前仅支持一个文件 <br/>
 * 文件中需要定义写入的最后position以及最后操作的时间戳
 * </p>
 *
 * @author 伍磊
 */
public interface FileOperations extends AutoCloseable {

    void init() throws Exception;

    void appendCommand(Command command);

    // todo 需要考虑数据过大带来的影响
    byte[] scan(Long timestamp) throws IOException;

}
