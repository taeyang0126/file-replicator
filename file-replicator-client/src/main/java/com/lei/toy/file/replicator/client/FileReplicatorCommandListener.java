package com.lei.toy.file.replicator.client;

import com.lei.toy.file.replicator.core.command.Command;

/**
 * <p>
 * FileReplicatorCommandListener
 * </p>
 *
 * @author 伍磊
 */
public interface FileReplicatorCommandListener {

    void handler(Command command);

}
