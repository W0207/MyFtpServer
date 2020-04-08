package com.abee.ftp.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author xincong yao
 */
public class MyFtpClient {

    private Socket socket;

    private BasicOperationSet operationSet;

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connected to " + socket.getRemoteSocketAddress());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        operationSet = new AdvancedOperationSet(out, in);
    }

    /**
     * @param local directory or uri.
     * @param remote should always be a working directory.
     * @return result of uploading.
     */
    public boolean upload(String local, String remote) throws IOException, ClassNotFoundException {
        File file = new File(local);

        operationSet.cwd(remote);

        return operationSet.uploads(file);
    }

    /**
     * @param remote directory or uri,
     *               thus {@link com.abee.ftp.client.BasicOperationSet#downloads(File, String)}
     *               needs two parameters.
     * @param local should always be a directory.
     * @return result of downloading.
     */
    public boolean download(String remote, String local) throws IOException, ClassNotFoundException {
        File file = new File(local);

        return operationSet.downloads(file, remote);
    }

}
