package com.lei.toy.file.replicator.server.properties;

import io.vavr.control.Try;
import lombok.Getter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.URL;

/**
 * <p>
 * FileReplicatorProperties
 * </p>
 *
 * @author 伍磊
 */
@Getter
public class FileReplicatorServerProperties implements EnvironmentAware {

    private Environment environment;

    // 存放目录
    private String dir;
    // 文件名称
    private String fileName;
    // 文件后缀
    private String fileExt;
    // 最大文件大小
    private long fileMaxSize;

    public FileReplicatorServerProperties() {
        // 加载properties，子类可进行重写
        loadProperties();
    }

    // 留给子类重写的方法
    protected void loadProperties() {
        String resourcePath = Try.of(() -> {
                    URL resource = this.getClass().getClassLoader().getResource("");
                    return resource.getPath();
                })
                .map(t -> {
                    if (!t.endsWith(File.separator)) {
                        return t + File.separator;
                    }
                    return t;
                })
                .getOrElseThrow(() -> new RuntimeException("resource find error!"));

        this.dir = resourcePath + environment.getProperty("file.replicator.server.file.dir", "file-replicator");
        this.fileName = environment.getProperty("file.replicator.server.file.name", "");
        this.fileExt = environment.getProperty("file.replicator.server.file.ext", ".txt");
        this.fileMaxSize = Try.of(() -> environment.getProperty("file.replicator.server.file.max.size", String.valueOf(1024 * 1024 * 1024)))
                .map(Long::parseLong)
                .getOrElseThrow(() -> new IllegalArgumentException("file max size error!"));

/*        this.nodeType = Try.of(() -> System.getProperty("file.replicator.node.type", String.valueOf(NodeTypeEnum.SERVER.getCode())))
                .map(Integer::parseInt)
                .map(NodeTypeEnum::of)
                .getOrElseThrow(() -> new IllegalArgumentException("node type error!"));*/

    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
