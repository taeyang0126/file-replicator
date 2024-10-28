package com.lei.toy.file.replicator.server.controller;

import com.lei.toy.file.replicator.core.api.FileReplicatorService;
import com.lei.toy.file.replicator.server.file.FileOperations;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 * FileReplicatorServerController
 * </p>
 *
 * @author 伍磊
 */
@RestController
@AllArgsConstructor
public class FileReplicatorServerController implements FileReplicatorService {

    private final FileOperations fileOperations;

    @Override
    public ResponseEntity<byte[]> sync(@RequestBody(required = false) Long timestamp) throws IOException {
        byte[] data = fileOperations.scan(timestamp);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }
}

