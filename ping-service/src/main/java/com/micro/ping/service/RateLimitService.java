package com.micro.ping.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@Service
public class RateLimitService {

    private static final int LIMIT = 2;
    private static final String LOCK_FILE = "ping-rate-limit.lock";
    private static final long WINDOW_MS = 1000;
    private static final int STATE_SIZE = 12; // long windowStart + int counter

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;

    @PostConstruct
    public void init() throws Exception {
        randomAccessFile = new RandomAccessFile(LOCK_FILE, "rw");
        fileChannel = randomAccessFile.getChannel();
        if (fileChannel.size() < STATE_SIZE) {
            fileChannel.write(ByteBuffer.allocate(STATE_SIZE), 0);
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (fileChannel != null) {
            fileChannel.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    public synchronized boolean tryAcquire() {
        try (FileLock ignored = fileChannel.lock()) {
            if (ignored == null) {
                return false;
            }

            ByteBuffer buffer = ByteBuffer.allocate(STATE_SIZE);
            fileChannel.read(buffer, 0);
            buffer.flip();
            long windowStart = buffer.getLong();
            int counter = buffer.getInt();

            long now = System.currentTimeMillis();
            if (windowStart == 0 || now - windowStart >= WINDOW_MS) {
                windowStart = now;
                counter = 0;
            }
            counter++;
            boolean allowed = counter <= LIMIT;

            buffer.clear();
            buffer.putLong(windowStart);
            buffer.putInt(counter);
            buffer.flip();
            fileChannel.write(buffer, 0);
            return allowed;
        } catch (Exception e) {
            return false;
        }
    }
}
