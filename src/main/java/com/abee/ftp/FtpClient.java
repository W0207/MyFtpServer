package com.abee.ftp;

import com.abee.ftp.client.AdvancedOperationSet;
import com.abee.ftp.client.BasicOperationSet;
import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.RequestCommand;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.tool.FileTransferUtil;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

public class FtpClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //testUploadFolder();
        testDownloadFolder();
    }

    public static void testUploadFolder() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String local = "D:/OTHER/temp";
        File file = new File(local);

        AdvancedOperationSet operationSet = new AdvancedOperationSet(out, in);

        operationSet.uploads(file);
    }

    public static void testDownloadFolder() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String remote = "D:/OTHER/test4cn/ftp";
        File file = new File("D:/OTHER/temp");

        AdvancedOperationSet operationSet = new AdvancedOperationSet(out, in);

        operationSet.downloads(file, remote);
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

    public static void testDownload() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        InputStream input = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.PASV));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.RETR, "ftp-example.zip"));
        ResponseBody response3 = (ResponseBody) in.readObject();

        File file = new File("D:/OTHER/temp/ftp-example.zip");

        BasicOperationSet bos = new BasicOperationSet(out, in);
        bos.download(file, response2.getPassivePort());

        System.out.println(response1 + "\n" + response2 + "\n" + response3);
    }

    public static void testUpload(String src, String tar) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        /**
         * Send commands.
         */
        InputStream input = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.CWD, "D:/OTHER/test4cn/ftp"));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.PASV));
        ResponseBody response3 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.STOR, tar));
        ResponseBody response4 = (ResponseBody) in.readObject();

        Socket dataSocket = new Socket("localhost", response3.getPassivePort());

        OutputStream dataStream = dataSocket.getOutputStream();
        File file = new File(src);

        FileTransferUtil.file2Stream(dataStream, file);

        ResponseBody response5 = (ResponseBody) in.readObject();
        System.out.println(response1 + "\n" + response2 + "\n" + response3 + "\n" + response4 + "\n" + response5);
    }

    public static void testSizeAndMkd() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        /**
         * Send commands.
         */
        InputStream input = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.CWD, "D:/OTHER/test4cn/ftp"));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.SIZE, "test3.jpeg"));
        ResponseBody response3 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.MKD, "test-mkd"));
        ResponseBody response4 = (ResponseBody) in.readObject();
        System.out.println(response1 + "\n" + response2 + "\n" + response3 + "\n" + response4);
    }

    public static void testList() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        /**
         * Send commands.
         */
        InputStream input = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response1 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.CWD, "D:/OTHER/test4cn/ftp"));
        ResponseBody response2 = (ResponseBody) in.readObject();
        out.writeObject(new RequestBody(RequestCommand.LIST));
        ResponseBody response4 = (ResponseBody) in.readObject();
        System.out.println(response1 + "\n" + response2 + "\n" + response4);
    }
}
