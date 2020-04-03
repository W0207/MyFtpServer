package com.abee.ftp;

import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.RequestCommand;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.tool.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FtpClient {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        InputStream input = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.PASV));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.RETR, "D:/OTHER/test4cn/ftp/test3.jpeg"));
        ResponseBody response3 = (ResponseBody) in.readObject();

        Socket dataSocket = new Socket("localhost", response2.getPassivePort());

        InputStream dataStream = dataSocket.getInputStream();
        List<byte[]> batches = new ArrayList<>();
        int len;
        int totalLen = 0;
        byte[] buffer = new byte[1024];
        while ((len = dataStream.read(buffer)) != -1) {
            byte[] t = new byte[len];
            System.arraycopy(buffer, 0, t, 0, len);
            batches.add(t);
            totalLen += len;
        }

        len = 0;
        byte[] result = new byte[totalLen];
        for (byte[] data: batches) {
            System.arraycopy(data, 0, result, len, data.length);
            len += data.length;
        }
        FileUtil.write(result, "D:/OTHER/temp/test3.jpeg");

        System.out.println(response1 + " " + response2 + " " + response3);

    }

    public static void testRequest() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.CWD, "D:/OTHER/test4cn"));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response3 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.TYPE, "A"));
        ResponseBody response4 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.PASV));
        ResponseBody response5 = (ResponseBody) in.readObject();

        System.out.println(response1 + " " + response2 + " " + response3 + " " + response4 + " " + response5);

        socket.close();
    }
}
