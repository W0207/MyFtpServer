package com.abee.ftp.common.listener;

import com.abee.ftp.common.handler.CommandHandler;
import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.config.ServerContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author xincong yao
 */
public class ServerCommandListener extends CommandListener {

    public boolean start = true;

    public ServerCommandListener(String hostname, int port) throws UnknownHostException {
        super.init(hostname, port);
    }

    @Override
    public void run() {
        try {
            if (socketAddress == null) {
                throw new Exception("Server socket address has not been set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(socketAddress.getPort(), 50, socketAddress.getAddress());
            System.out.println("FTP Server started. IP: " + socketAddress.getAddress()
                    + ", Listener Port: " + socketAddress.getPort());
        } catch (IOException e) {
            e.printStackTrace();
            start = false;
        }

        while (start) {
            Socket client;

            try {
                client = serverSocket.accept();

                System.out.println("Client connected. IP: " + client.getInetAddress()
                        + ", Port: " + client.getPort());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Worker worker = new Worker(client);
            worker.start();
        }
    }

    /**
     * Main loop to process client commands.
     */
    public class Worker extends Thread {

        private Socket socket;

        private String directory = ServerContext.getRoot();

        private String serverMode = ServerContext.getServerMode();

        private String transferMode = ServerContext.getTransferMode();

        private ServerSocket dataSocket;

        private InputStream in;

        private OutputStream out;

        private ObjectOutputStream objectOut;

        private ObjectInputStream objectIn;

        public Worker(Socket socket) {
            this.socket = socket;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                objectIn = new ObjectInputStream(in);
                objectOut = new ObjectOutputStream(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (socket.isConnected()) {
                try {
                    RequestBody request = (RequestBody) objectIn.readObject();

                    ResponseBody response = CommandHandler.process(request, this);

                    objectOut.writeObject(response);
                } catch (Exception e) {
                    System.out.println("Client disconnect." +
                            " IP: " + socket.getInetAddress()
                            + ", Port: " + socket.getPort());
                    break;
                }
            }
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getServerMode() {
            return serverMode;
        }

        public void setServerMode(String serverMode) {
            this.serverMode = serverMode;
        }

        public String getTransferMode() {
            return transferMode;
        }

        public void setTransferMode(String transferMode) {
            this.transferMode = transferMode;
        }

        public OutputStream getOut() {
            return out;
        }

        public void setOut(OutputStream out) {
            this.out = out;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public InputStream getIn() {
            return in;
        }

        public void setIn(InputStream in) {
            this.in = in;
        }

        public ObjectOutputStream getObjectOut() {
            return objectOut;
        }

        public void setObjectOut(ObjectOutputStream objectOut) {
            this.objectOut = objectOut;
        }

        public ObjectInputStream getObjectIn() {
            return objectIn;
        }

        public void setObjectIn(ObjectInputStream objectIn) {
            this.objectIn = objectIn;
        }

        public ServerSocket getDataSocket() {
            return dataSocket;
        }

        public void setDataSocket(ServerSocket dataSocket) {
            this.dataSocket = dataSocket;
        }
    }
}
