package com.abee.ftp.client;

import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.RequestCommand;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.tool.FileTransferUtil;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xincong yao
 */
public class BasicOperationSet {
    /**
     * To send requests.
     */
    ObjectOutputStream out;

    /**
     * To receive responses.
     */
    ObjectInputStream in;

    public BasicOperationSet(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public ResponseBody pwd() throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.PWD));
        return (ResponseBody) in.readObject();
    }

    /**
     * @param arg the complete working directory of server.
     */
    public ResponseBody cwd(String arg) throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.CWD, arg));
        return (ResponseBody) in.readObject();
    }

    public Map<String, Boolean> list() throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.LIST));
        ResponseBody response = (ResponseBody) in.readObject();
        String[] entries = response.getArg().split("\\?");
        Map<String, Boolean> files = new HashMap<>(entries.length);
        for (String entry: entries) {
            String[] t = entry.split(":");
            /**
             * "0": common file  ->   false
             * "1": directory    ->   true
             */
            if (t.length == 2) {
                files.put(t[0], "1".equals(t[1]));
            }
        }
        return files;
    }

    /**
     * @param arg The name of local folder.
     */
    public ResponseBody mkd(String arg) throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.MKD, arg));
        return (ResponseBody) in.readObject();
    }

    public ResponseBody pasv() throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.PASV));
        return (ResponseBody) in.readObject();
    }

    public ResponseBody stor(File file) throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.STOR, file.getName()));
        return (ResponseBody) in.readObject();
    }

    public ResponseBody retr(String name) throws IOException, ClassNotFoundException {
        out.writeObject(new RequestBody(RequestCommand.RETR, name));
        return (ResponseBody) in.readObject();
    }

    /**
     * Upload common file.
     */
    public ResponseBody upload(File file, int port) throws IOException, ClassNotFoundException {
        Socket dataSocket = new Socket("localhost", port);
        OutputStream dataStream = dataSocket.getOutputStream();
        FileTransferUtil.file2Stream(dataStream, file);
        return (ResponseBody) in.readObject();
    }

    /**
     * Download common file.
     */
    public ResponseBody download(File file, int port) throws IOException, ClassNotFoundException {
        Socket dataSocket = new Socket("localhost", port);
        InputStream dataStream = dataSocket.getInputStream();
        FileTransferUtil.stream2File(dataStream, file);
        return (ResponseBody) in.readObject();
    }
}
