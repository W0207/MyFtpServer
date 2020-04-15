package com.abee.ftp.common.tunnel;


import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileTransferUtil;

import java.io.*;
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
        try {
            File file = new File(uri);
            OutputStream out = client.getOutputStream();
            if (FileTransferUtil.file2Stream(out, file)) {
                notification.writeObject(new ResponseBody(ResponseCode._226, ResponseCode._226.description));
            } else {
                notification.writeObject(new ResponseBody(ResponseCode._500, "Transfer failed."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
