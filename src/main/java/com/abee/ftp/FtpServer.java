package com.abee.ftp;


import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.secure.AuthorityCenter;
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

        server.setRoot("D:/server");

        AuthorityCenter certificateAuthority = new AuthorityCenter("localhost", 2222);
        server.setAuthorityCenter(certificateAuthority);

        server.start();
    }
}
