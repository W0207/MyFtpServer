package com.abee.ftp;


import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.server.MyFtpServer;

import java.net.UnknownHostException;

/**
 * @author xincong yao
 */
public class FtpServer {

    public static void main(String[] args) throws UnknownHostException {
        MyFtpServer server = new MyFtpServer();

        ServerCommandListener serverCommandListener = new ServerCommandListener("localhost", 2221);
        server.setCommandListener(serverCommandListener);

        server.start();
    }
}
