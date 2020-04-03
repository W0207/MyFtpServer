package com.abee.ftp.test;

import com.abee.ftp.common.state.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(7236);
        Socket client = server.accept();
        InputStream in = client.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(in);
        while (client.isConnected()) {
            ResponseBody rb = null;
            try {
                rb = (ResponseBody) oin.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            System.out.println(rb);
        }
    }
}
