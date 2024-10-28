package com.lei.toy.file.replicator.core.config;

import com.lei.toy.file.replicator.core.util.Compressor;
import com.lei.toy.file.replicator.core.util.GZipCompressor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * FileReplicatorConfiguration
 * </p>
 *
 * @author 伍磊
 */
@Configuration
public class FileReplicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(Compressor.class)
    public Compressor compressor() {
        return new GZipCompressor();
    }
}
