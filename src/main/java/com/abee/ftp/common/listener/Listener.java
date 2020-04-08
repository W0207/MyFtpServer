package com.abee.ftp.common.listener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * @author xincong yao
 */
public abstract class Listener extends Thread {

    protected InetSocketAddress socketAddress;

    public void init(String hostname, int port) throws UnknownHostException {
        socketAddress = new InetSocketAddress(InetAddress.getByName(hostname), port);
    }
}
