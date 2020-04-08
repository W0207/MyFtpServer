package com.abee.ftp.test;

import com.abee.ftp.common.tool.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileWriter extends Thread {

    private final Object lock = new Object();

    private String name;

    private String result;

    private String uri;

    public FileWriter(String result, String uri, String name) {
        this.result = result;
        this.uri = uri;
        this.name = name;
    }

    @Override
    public void run() {
        FileChannel channel = null;
        ByteBuffer buffer = ByteBuffer.wrap(result.getBytes());

        try {
            channel = new FileOutputStream(uri).getChannel();

            synchronized (lock) {
                System.out.println(name + " write");
                channel.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    synchronized (lock) {
                        System.out.println(name + " close");
                        channel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
