package com.lei.toy.file.replicator.client;

import com.lei.toy.file.replicator.core.api.FileReplicatorService;
import com.lei.toy.file.replicator.core.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * FileReplicatorClient
 * </p>
 *
 * @author 伍磊
 */
@Slf4j
public class FileReplicatorClient implements Runnable {

    private final FileReplicatorService fileReplicatorService;
    private final FileReplicatorCommandListener fileReplicatorCommandListener;
    private long syncTimestamp = 0L;
    private Duration pollInterval;
    private LinkedBlockingDeque<Command> commandQueue = new LinkedBlockingDeque<>();
    private ScheduledExecutorService pollExecutor = Executors.newSingleThreadScheduledExecutor(
            new CustomizableThreadFactory("file.replicator.client.poll-%s")
    );
    private Thread listenerThread;

    public FileReplicatorClient(FileReplicatorService fileReplicatorService, FileReplicatorCommandListener fileReplicatorCommandListener, FileReplicatorClientProperties fileReplicatorClientProperties) {
        this.fileReplicatorService = fileReplicatorService;
        this.fileReplicatorCommandListener = fileReplicatorCommandListener;
        this.pollInterval = fileReplicatorClientProperties.getPollInterval();
        pollExecutor.scheduleAtFixedRate(this::poll, 0, pollInterval.getSeconds(), TimeUnit.SECONDS);
        listenerThread = new Thread(this::run, "file.replicator.client.listener");
        listenerThread.start();
    }

    public void poll() {
        try {
            ResponseEntity<byte[]> syncResponse = fileReplicatorService.sync(this.syncTimestamp);
            byte[] data = syncResponse.getBody();
            if (data == null || data.length == 0) {
                return;
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            int curPos = 0;

        } catch (Exception e) {
            log.error("file replicator poll error, lastSyncTimestamp={}, e=", syncTimestamp, e);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Command command = null;
            try {
                command = commandQueue.takeFirst();
                fileReplicatorCommandListener.handler(command);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("command handler error, e: ", e);
                if (command != null) {
                    try {
                        commandQueue.putFirst(command);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        log.info("thread interrupted: {}", Thread.currentThread().isInterrupted());
    }
}
