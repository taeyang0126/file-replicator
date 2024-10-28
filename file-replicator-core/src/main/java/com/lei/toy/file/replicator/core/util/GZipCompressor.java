package com.lei.toy.file.replicator.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * GZipCompressor
 * </p>
 *
 * @author 伍磊
 */
public class GZipCompressor extends AbstractCompressor {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        validateInput(data);

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {

            gzipStream.write(data);
            gzipStream.finish();

            return byteStream.toByteArray();
        }
    }

    @Override
    public byte[] decompress(byte[] data) throws IOException {
        validateInput(data);

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             GZIPInputStream gzipStream = new GZIPInputStream(byteStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = gzipStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        }
    }
}
