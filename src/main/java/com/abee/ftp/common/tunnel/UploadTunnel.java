package com.abee.ftp.common.tunnel;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;


/**
 * Client upload file to server.
 *
 * @author yaoxi
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
        File file = new File(uri);

        FileOutputStream fos;
        BufferedOutputStream bos;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
        } catch (FileNotFoundException e) {
            System.out.println("File " + uri + " can't be written.");
            return;
        }

        InputStream in = null;
        try {
            in = client.getInputStream();
            int len;
            while ((len = in.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            notification.writeObject(new ResponseBody(ResponseCode._226, ResponseCode._226.description));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                bos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
