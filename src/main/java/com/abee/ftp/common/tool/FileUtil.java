package com.abee.ftp.common.tool;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileUtil {

    public static boolean clean(String path) throws IOException {
        File file = new File(path);
        return clean(file);
    }

    public static boolean clean(File file) throws IOException {

        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();
        return true;
    }

    public static boolean write(byte[] result, String path) {
        FileChannel channel = null;
        FileLock lock = null;
        ByteBuffer buffer = ByteBuffer.wrap(result);

        try {
            channel = new FileOutputStream(path).getChannel();

            /**
             * Try to get file lock every 0 ~ 60ms, it will try 3 times.
             */
            int times = 0;
            while (times < 3 && lock == null) {
                try {
                    lock = channel.tryLock();
                    clean(path);
                } catch (OverlappingFileLockException e) {
                    times++;
                    Thread.sleep(new Random().nextInt(60));
                }
            }
            if (lock == null) {
                return false;
            }

            channel.write(buffer);
            return true;
        } catch (IOException | InterruptedException e) {
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
        }

        return false;
    }

    private static byte[] read(File file){
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            byte[] buffer = new byte[4096];
            List<byte[]> batches = new ArrayList<>();

            int len;
            int totalLen = 0;
            while ((len = bis.read(buffer)) != -1) {
                byte[] t = new byte[len];
                System.arraycopy(buffer, 0, t, 0, len);
                batches.add(t);
                totalLen += len;
            }

            len = 0;
            byte[] result = new byte[totalLen];
            for (byte[] data: batches) {
                System.arraycopy(data, 0, result, len, data.length);
                len += data.length;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean delete(String path){
        File file = new File(path);
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public void toBrowser(HttpServletResponse response, String filename){
        if (filename != null) {
            File file = new File(filename);
            if (file.exists()) {
                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;filename=" + filename);
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();

                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void toLocal(File file, String path){
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        OutputStream os = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];

            os = new FileOutputStream(path);

            int len;
            while ((len = bis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                os.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File multiPartFile2File(MultipartFile multipartFile){
        File f = new File(multipartFile.getOriginalFilename());
        InputStream in = null;
        OutputStream os = null;
        try {
            in  = multipartFile.getInputStream();
            os = new FileOutputStream(f);

            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return f;
    }
}
