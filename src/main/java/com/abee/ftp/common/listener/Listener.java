package com.abee.ftp.common.listener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author xincong yao
 */
public abstract class Listener extends Thread {

    protected InetSocketAddress socketAddress;

    protected static int bufferSize = 512;

    public void init(String hostname, int port) {
        socketAddress = new InetSocketAddress(hostname, port);
    }
}
