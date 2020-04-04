package com.abee.ftp.common.tunnel;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tool.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author xincong yao
 */
public abstract class DataTunnel extends Thread {

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
