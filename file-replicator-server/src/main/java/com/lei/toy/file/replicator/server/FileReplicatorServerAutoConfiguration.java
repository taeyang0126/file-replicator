package com.lei.toy.file.replicator.server;

import com.lei.toy.file.replicator.core.api.FileReplicatorService;
import com.lei.toy.file.replicator.core.util.Compressor;
import com.lei.toy.file.replicator.server.controller.FileReplicatorServerController;
import com.lei.toy.file.replicator.server.file.FileOperations;
import com.lei.toy.file.replicator.server.file.GenericFileOperations;
import com.lei.toy.file.replicator.server.properties.FileReplicatorServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * TODO
 * </p>
 *
 * @author 伍磊
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "file.replicator.server", name = "enable", havingValue = "true")
public class FileReplicatorServerAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(FileReplicatorServerProperties.class)
    public FileReplicatorServerProperties fileReplicatorServerProperties() {
        return new FileReplicatorServerProperties();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(FileOperations.class)
    public FileOperations fileOperations(FileReplicatorServerProperties fileReplicatorServerProperties,
                                         Compressor compressor) {
        return new GenericFileOperations(fileReplicatorServerProperties, compressor);
    }

    @Bean
    @ConditionalOnMissingBean(FileReplicatorService.class)
    public FileReplicatorService fileReplicatorService(FileOperations fileOperations) {
        return new FileReplicatorServerController(fileOperations);
    }

}
