package com.lei.toy.file.replicator.core.util;

import java.io.IOException;

/**
 * <p>
 * Compressor
 * </p>
 *
 * @author 伍磊
 */
public interface Compressor {
    byte[] compress(byte[] data) throws IOException;
    byte[] decompress(byte[] data) throws IOException;
}
