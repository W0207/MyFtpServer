package com.abee.ftp.common.handler;

import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.common.state.RequestBody;
import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;
import com.abee.ftp.common.tunnel.DataTunnel;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author xincong yao
 */
public class CommandHandler {

    public static ResponseBody process(RequestBody request, ServerCommandListener.Worker worker) {
        ResponseBody response = null;
        switch (request.getCommand()) {
            case PWD:
                response = printWorkDictionary(request, worker);
                break;
            case CWD:
                response = changeWorkDictionary(request, worker);
                break;
            case LIST:
                response = transferFileList(request, worker);
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
                response = startTransfer(request, worker);
            default:
        }

        return response;
    }

    private static ResponseBody startTransfer(RequestBody request, ServerCommandListener.Worker worker) {
        /**
         * Set data tunnel context, ready to transfer.
         */

        if (worker.getDataTunnel() != null) {
            worker.getDataTunnel().setUri(request.getArg());
            worker.getDataTunnel().start();
        }

        return new ResponseBody(ResponseCode._150, "Opening data connection.");
    }

    /**
     * todo: Implement getFileSize function.
     */
    private static ResponseBody getFileSize(RequestBody request, ServerCommandListener.Worker worker) {
        return null;
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
        if (worker.getDataTunnel() != null && !worker.getDataTunnel().getServerSocket().isClosed()) {
            return new ResponseBody(ResponseCode._227, "Entering passive mode.",
                    worker.getDataTunnel().getServerSocket().getInetAddress().toString(),
                    worker.getDataTunnel().getServerSocket().getLocalPort());
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

        DataTunnel dataTunnel = new DataTunnel(serverSocket, worker.getObjectOut());
        worker.setDataTunnel(dataTunnel);

        return new ResponseBody(ResponseCode._227, "Entering passive mode.",
                worker.getDataTunnel().getServerSocket().getInetAddress().toString(),
                worker.getDataTunnel().getServerSocket().getLocalPort());
    }

    /**
     * todo: Implement transferFileList function.
     */
    private static ResponseBody transferFileList(RequestBody request, ServerCommandListener.Worker worker) {
        return null;
    }

    private static ResponseBody changeWorkDictionary(RequestBody request, ServerCommandListener.Worker worker) {
        ResponseBody response;

        File file = new File(request.getArg());
        if (file.isDirectory()) {
            worker.setDictionary(request.getArg());
            response = new ResponseBody(ResponseCode._250, "Dictionary changed to " + request.getArg() + ".");

        } else {
            response = new ResponseBody(ResponseCode._500, "Dictionary " + request.getArg() + " not exist.");
        }

        return response;
    }

    private static ResponseBody printWorkDictionary(RequestBody request, ServerCommandListener.Worker worker) {
        ResponseBody response = new ResponseBody(ResponseCode._257, worker.getDictionary());
        return response;
    }

}
