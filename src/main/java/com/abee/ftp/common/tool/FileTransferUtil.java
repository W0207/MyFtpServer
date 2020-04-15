package com.abee.ftp.common.tool;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author xincong yao
 */
public class FileTransferUtil {

    public static boolean file2Stream(OutputStream out, File file) {
        int len;
        byte[] buffer = new byte[4096];

        FileInputStream fis;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            while ((len = bis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static boolean stream2File(InputStream in, File file) {
        FileChannel channel = null;
        FileLock lock = null;
        try {
            channel = new FileOutputStream(file).getChannel();

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
                    channel.write(ByteBuffer.wrap("".getBytes()));
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

            /**
             * Write bytes from stream to file.
             */
            int len;
            byte[] buffer = new byte[4096];
            while ((len = in.read(buffer)) != -1) {
                channel.write(ByteBuffer.wrap(buffer, 0, len));
            }

        } catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " can't be written.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
        return true;
    }
}
