package com.abee.ftp.common.handler;

import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tunnel.*;
import com.abee.ftp.config.ServerContext;
import com.abee.ftp.secure.KeyStore;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

/**
 * @author xincong yao
 */
public class CommandHandler {

    public static ResponseBody process(RequestBody request, ServerCommandListener.Worker worker) {
        ResponseBody response = null;
        switch (request.getCommand()) {
            case PWD:
                response = printWorkDirectory(worker);
                break;
            case CWD:
                response = changeWorkDirectory(request, worker);
                break;
            case LIST:
                response = transferFileList(worker);
                break;
            case PASV:
                response = turnToPassiveMode(worker);
                break;
            case PORT:
                response = turnToPositiveMode(worker);
                break;
            case TYPE:
                response = changeTransferType(request, worker);
                break;
            case SIZE:
                response = getFileSize(request, worker);
                break;
            case RETR:
            case STOR:
            case RETRS:
            case STORS:
                response = startTransfer(request, worker);
                break;
            case MKD:
                response = makeDirectory(request, worker);
                break;
            case MD5:
                response = getMd5(request, worker);
                break;
            default:
        }

        return response;
    }

    private static ResponseBody getMd5(RequestBody request, ServerCommandListener.Worker worker) {
        String uri = worker.getDirectory() + "/" + request.getArg();
        try {
            FileInputStream fi = new FileInputStream(uri);
            String md5Hex = DigestUtils.md5Hex(fi);
            fi.close();
            return new ResponseBody(ResponseCode._200, md5Hex);
        } catch (IOException e) {
            return new ResponseBody(ResponseCode._200);
        }
    }

    private static ResponseBody makeDirectory(RequestBody request, ServerCommandListener.Worker worker) {
        String pathname = worker.getDirectory() + "/" + request.getArg();
        File file = new File(pathname);
        if (!file.exists()) {
            if (file.mkdir()) {
                return new ResponseBody(ResponseCode._257, pathname + " Directory created.");
            } else {
                return new ResponseBody(ResponseCode._500, pathname + " illegal.");
            }
        } else {
            return new ResponseBody(ResponseCode._257, pathname + " already exists.");
        }
    }

    private static ResponseBody startTransfer(RequestBody request, ServerCommandListener.Worker worker) {
        /**
         * Set data tunnel context, ready to transfer.
         */
        DataTunnel tunnel = null;
        switch (request.getCommand()) {
            case RETR:
                tunnel = new DownloadTunnel(worker.getDataSocket(), worker.getObjectOut());
                break;
            case STOR:
                tunnel = new UploadTunnel(worker.getDataSocket(), worker.getObjectOut());
                break;
            case RETRS:
                String address = worker.getSocket().getInetAddress().toString();
                byte[] key = KeyStore.getKeys().get(address);
                tunnel = new SecureDownloadTunnel(
                        worker.getDataSocket(), worker.getObjectOut(), key);
                break;
            case STORS:
                address = worker.getSocket().getInetAddress().toString();
                key = KeyStore.getKeys().get(address);
                tunnel = new SecureUploadTunnel(
                        worker.getDataSocket(), worker.getObjectOut(), key);
            default:
        }

        if (tunnel != null) {
            tunnel.setUri(worker.getDirectory() + "/" + request.getArg());
            tunnel.start();
            return new ResponseBody(ResponseCode._150, "Opening data connection.");
        } else {
            return new ResponseBody(ResponseCode._500, "Failed.");
        }
    }

    private static ResponseBody getFileSize(RequestBody request, ServerCommandListener.Worker worker) {
        String pathname = worker.getDirectory() + "/" + request.getArg();
        File file = new File(pathname);
        if (file.exists() && file.isFile()) {
            return new ResponseBody(ResponseCode._213, String.valueOf(file.length()));
        } else {
            return new ResponseBody(ResponseCode._500, pathname + " not exists.");
        }

    }

    private static ResponseBody changeTransferType(RequestBody request, ServerCommandListener.Worker worker) {
        if ("A".equals(request.getArg()) || "I".equals(request.getArg())) {
            worker.setTransferMode(request.getArg());
            return new ResponseBody(ResponseCode._200, "Type set to " + request.getArg() + ".");
        } else {
            return new ResponseBody(ResponseCode._500, "Type " + request.getArg() + " not available.");
        }

    }

    /**
     * todo: Implement positive transfer mode.
     */
    private static ResponseBody turnToPositiveMode(ServerCommandListener.Worker worker) {
        return null;
    }

    private static ResponseBody turnToPassiveMode(ServerCommandListener.Worker worker) {
        worker.setServerMode("PASV");

        /**
         * Response directly if data tunnel already opened.
         */
        if (worker.getDataSocket() != null && !worker.getDataSocket().isClosed()) {
            return new ResponseBody(ResponseCode._227, "Entering passive mode.",
                    worker.getDataSocket().getInetAddress().toString(),
                    worker.getDataSocket().getLocalPort());
        }

        /**
         * Start a thread to wait for client to connect,
         * then begin data transfer.
         */
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            return new ResponseBody(ResponseCode._500, "No more port available.");
        }

        worker.setDataSocket(serverSocket);

        return new ResponseBody(ResponseCode._227, "Entering passive mode.",
                serverSocket.getInetAddress().toString(),
                serverSocket.getLocalPort());
    }

    private static ResponseBody transferFileList(ServerCommandListener.Worker worker) {
        File file = new File(worker.getDirectory());
        StringBuilder sb = new StringBuilder();
        for (File f: Objects.requireNonNull(file.listFiles())) {
            if (f.isDirectory()) {
                sb.append(f.getName()).append(":1?");
            } else {
                sb.append(f.getName()).append(":0?");
            }
        }
        return new ResponseBody(ResponseCode._200, sb.toString());
    }

    private static ResponseBody changeWorkDirectory(RequestBody request, ServerCommandListener.Worker worker) {
        ResponseBody response;

        if (request.getArg().length() < ServerContext.getRoot().length() ||
                !request.getArg().substring(0, ServerContext.getRoot().length()).equals(ServerContext.getRoot())) {
            return new ResponseBody(ResponseCode._500, "Don't get smart with me, you don't have the equipment.");
        }

        File file = new File(request.getArg());
        if (file.isDirectory()) {
            worker.setDirectory(request.getArg());
            response = new ResponseBody(ResponseCode._250, "Directory changed to " + request.getArg() + ".");

        } else {
            response = new ResponseBody(ResponseCode._500, "Directory " + request.getArg() + " not exist.");
        }

        return response;
    }

    private static ResponseBody printWorkDirectory(ServerCommandListener.Worker worker) {
        ResponseBody response = new ResponseBody(ResponseCode._257, worker.getDirectory());
        return response;
    }

}
