package com.lei.toy.file.replicator.core.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * <p>
 * FileReplicatorService
 * </p>
 *
 * @author 伍磊
 */
@FeignClient(value = "${file.replicator.client.service.name}", url = "${file.replicator.client.service.url:}")
@RequestMapping("/file/replicator")
public interface FileReplicatorService {

    @PostMapping("/sync")
    ResponseEntity<byte[]> sync(@RequestBody(required = false) Long timestamp) throws IOException;

}
