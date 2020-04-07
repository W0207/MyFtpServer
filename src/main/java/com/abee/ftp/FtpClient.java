package com.abee.ftp;

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
//        for (int i = 0; i < 10; i++) {
//            new Thread(
//                    new Runnable() {
//                        private int i = new Random().nextInt(10);
//                        @Override
//                        public void run() {
//                            try {
//                                testUpload("D:/OTHER/temp/" + i + ".txt",  "tar.txt");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (ClassNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//            ).start();
//        }
//        testDownload();
        testUploadFolder();
        //testUpload("D:/OTHER/temp/1.txt",  "tar.txt");
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

        Socket dataSocket = new Socket("localhost", response2.getPassivePort());

        InputStream dataStream = dataSocket.getInputStream();
        File file = new File("D:/OTHER/temp/ftp-example.zip");
        FileTransferUtil.stream2File(dataStream, file);

        ResponseBody response4 = (ResponseBody) in.readObject();
        System.out.println(response1 + "\n" + response2 + "\n" + response3 + "\n" + response4);
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

    public static void testUploadFolder() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 2221);
        System.out.println("Connected: " + socket.isConnected());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String local = "D:/OTHER/temp";
        File file = new File(local);

        upload(out, in, file);
    }

    public static void upload(ObjectOutputStream out, ObjectInputStream in, File file) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            mkd(out, file.getName());
            ResponseBody response0 = (ResponseBody) in.readObject();
            for (File f: Objects.requireNonNull(file.listFiles())) {

                cwd(out, pwd(out, in) + "/" + file.getName());
                ResponseBody response1 = (ResponseBody) in.readObject();

                upload(out, in, f);
            }
        } else {
            out.writeObject(new RequestBody(RequestCommand.PASV));
            ResponseBody response = (ResponseBody) in.readObject();
            out.writeObject(new RequestBody(RequestCommand.STOR, file.getName()));
            ResponseBody response2 = (ResponseBody) in.readObject();

            Socket dataSocket = new Socket("localhost", response.getPassivePort());

            OutputStream dataStream = dataSocket.getOutputStream();
            FileTransferUtil.file2Stream(dataStream, file);
            ResponseBody response3 = (ResponseBody) in.readObject();
        }
    }

    public static void cwd(ObjectOutputStream out, String arg) throws IOException {
        out.writeObject(new RequestBody(RequestCommand.CWD, arg));
    }

    public static void mkd(ObjectOutputStream out, String arg) throws IOException {
        out.writeObject(new RequestBody(RequestCommand.MKD, arg));
    }

    public static String pwd(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.PWD));
        ResponseBody response = (ResponseBody) in.readObject();
        return response.getArg();
    }
}
