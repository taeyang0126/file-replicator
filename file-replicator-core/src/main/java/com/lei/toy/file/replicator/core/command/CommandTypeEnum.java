package com.lei.toy.file.replicator.core.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * CommandTypeEnum
 * </p>
 *
 * @author 伍磊
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CommandTypeEnum {

    ADD((byte) '0'),
    UPDATE((byte) '1'),
    DELETE((byte) '2'),

    ;

    private final byte code;

}
