package com.abee.ftp;


import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.server.MyFtpServer;

/**
 * @author xincong yao
 */
public class FtpServer {

    public static void main(String[] args) {
        MyFtpServer server = new MyFtpServer();

        ServerCommandListener serverCommandListener = new ServerCommandListener(2221);
        server.setCommandListener(serverCommandListener);

        server.start();
    }
}
