package com.abee.ftp;

import com.abee.ftp.client.MyFtpClient;

import java.io.*;

/**
 * @author xincong yao
 */
public class FtpClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MyFtpClient client = new MyFtpClient();
        client.connect("localhost", 2221);
        client.upload("D:/OTHER/client/root", "D:/OTHER/server/root");
        //client.download("D:/OTHER/server/root", "D:/OTHER/client/root");
    }
}
