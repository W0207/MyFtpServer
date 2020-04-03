package com.abee.ftp.common.tunnel;

import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.RequestCommand;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author xincong yao
 */
public class DataTunnel extends Thread {

    /**
     * To notified worker thread whether data transferring is done.
     */
    public ObjectOutputStream notification;

    public ServerSocket serverSocket;

    public Socket client;

    public String uri;

    public DataTunnel(ServerSocket serverSocket, ObjectOutputStream notification) {
        this.serverSocket = serverSocket;
        this.notification = notification;
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
         * todo: Transfer data.
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

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
