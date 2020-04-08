package com.abee.ftp.common.tunnel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xincong yao
 */
public abstract class DataTunnel extends Thread {

    /**
     * To notified worker thread whether data transferring is done.
     */
    ObjectOutputStream notification;

    ServerSocket serverSocket;

    Socket client;

    String uri;

    DataTunnel(ServerSocket serverSocket, ObjectOutputStream notification) {
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
