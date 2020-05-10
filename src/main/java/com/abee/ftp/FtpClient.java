package com.abee.ftp;

import com.abee.ftp.client.AdvancedOperationSet;
import com.abee.ftp.client.BasicOperationSet;
import com.abee.ftp.client.MyFtpClient;
import com.abee.ftp.client.secure.Authenticator;
import org.apache.commons.codec.DecoderException;

import java.io.*;

/**
 * @author xincong yao
 */
public class FtpClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Authenticator authenticator = new Authenticator();
        authenticator.connect("localhost", 2222);
        authenticator.authenticate();

        MyFtpClient client = new MyFtpClient();
        client.connect("localhost", 2221);
        client.upload("D:/OTHER/client/root", "D:/OTHER/server/root", false);
        //client.download("D:/OTHER/server/root", "D:/OTHER/client/root", true);
    }
}
