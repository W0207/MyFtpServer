package com.abee.ftp.common.tool;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

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

    public static byte[] read(File file){
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
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
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

    public static String write(byte[] result, String path) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            os.write(result);
            return "OK";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "ERROR";
    }

    public void delete(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }
}
