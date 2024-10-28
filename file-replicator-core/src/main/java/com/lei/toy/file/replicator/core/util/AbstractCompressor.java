package com.lei.toy.file.replicator.core.util;

/**
 * <p>
 * AbstractCompressor
 * </p>
 *
 * @author 伍磊
 */
public abstract class AbstractCompressor implements Compressor {

    protected int bufferSize = 8192; // 默认缓冲区大小

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    protected void validateInput(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
    }

}
