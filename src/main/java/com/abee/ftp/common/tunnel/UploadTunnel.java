package com.abee.ftp.common.tunnel;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Random;


/**
 * Client upload file to server.
 *
 * @author xincong yao
 */
public class UploadTunnel extends DataTunnel {
    public UploadTunnel(ServerSocket serverSocket, ObjectOutputStream notification) {
        super(serverSocket, notification);
    }

    @Override
    public void run() {
        /**
         * Client should connect server in a period of time,
         * otherwise close server to set port free.
         */
        try {
            serverSocket.setSoTimeout(6000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            System.out.println(e.getMessage() + ". Port: " + serverSocket.getLocalPort());
        }

        /**
         * Upload.
         */
        byte[] buffer = new byte[2048];

        FileChannel channel = null;
        FileLock lock = null;

        InputStream in = null;

        /**
         * To get file channel and lock.
         */
        try {
            channel = new FileOutputStream(uri).getChannel();

            /**
             * Try to get file lock every 0 ~ 60ms, it will try 3 times.
             */
            int times = 0;
            while (times < 3 && lock == null) {
                try {
                    lock = channel.tryLock();
                    /**
                     * Clean the origin file. Irreversible!
                     */
                    FileUtil.clean(uri);
                } catch (OverlappingFileLockException e) {
                    times++;
                    try {
                        Thread.sleep(new Random().nextInt(60));
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
            if (lock == null) {
                /**
                 * File can't be written.
                 */
                throw new FileNotFoundException();
            }

            in = client.getInputStream();
            int len;
            while ((len = in.read(buffer)) != -1) {
                channel.write(ByteBuffer.wrap(buffer, 0, len));
            }

            notification.writeObject(new ResponseBody(ResponseCode._226, ResponseCode._226.description));

        } catch (FileNotFoundException e) {
            System.out.println("File " + uri + " can't be written.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
