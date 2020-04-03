package com.abee.ftp.test;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 7236);
        OutputStream op = socket.getOutputStream();
        ObjectOutputStream oop = new ObjectOutputStream(op);
        for (int i = 0; i < 3; i++) {
            oop.writeObject(new ResponseBody(ResponseCode._257, "test"));
            Thread.sleep(3000);
        }
        socket.close();
    }
}
