package com.abee.ftp.common.tunnel;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileTransferUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;


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
        try {
            File file = new File(uri);
            InputStream in = client.getInputStream();
            if (FileTransferUtil.stream2File(in, file)) {
                notification.writeObject(new ResponseBody(ResponseCode._226, ResponseCode._226.description));
            } else {
                notification.writeObject(new ResponseBody(ResponseCode._500, "Transfer failed."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
