package com.abee.ftp.common.tunnel;


import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * Client download file from server.
 *
 * @author yaoxi
 */
public class DownloadTunnel extends DataTunnel {

    public DownloadTunnel(ServerSocket serverSocket, ObjectOutputStream notification) {
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
         * Download.
         */
        byte[] data = FileUtil.read(new File(uri));

        OutputStream out = null;
        try {
            out = client.getOutputStream();
            out.write(data);
            notification.writeObject(new ResponseBody(ResponseCode._226, ResponseCode._226.description));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
