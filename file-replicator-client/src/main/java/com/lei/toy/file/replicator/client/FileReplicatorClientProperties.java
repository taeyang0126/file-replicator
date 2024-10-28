package com.lei.toy.file.replicator.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * <p>
 * FileReplicatorClientProperties
 * </p>
 *
 * @author 伍磊
 */
@Data
@ConfigurationProperties(prefix = "file.replicator.client")
public class FileReplicatorClientProperties {

    private Duration pollInterval = Duration.ofSeconds(2);

}
