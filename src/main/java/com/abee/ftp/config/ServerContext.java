package com.abee.ftp.config;

import java.io.File;

/**
 * Only to initialize {@link com.abee.ftp.common.listener.ServerCommandListener}
 * You should only to set values before server started.
 *
 * @author xincong yao
 */
public class ServerContext {

    /**
     * Consider to read from xml file.
     */
    private static String root = "/";

    private static String history = "/";

    private static String transferMode = "I";

    private static String serverMode = "PASV";

    public static String getRoot() {
        return root;
    }

    public static boolean setRoot(String root) {
        if (new File(root).isDirectory()) {
            ServerContext.root = root;
            return true;
        } else {
            return false;
        }
    }

    public static String getHistory() {
        return history;
    }

    public static void setHistory(String history) {
        ServerContext.history = history;
    }

    public static String getTransferMode() {
        return transferMode;
    }

    public static void setTransferMode(String transferMode) {
        ServerContext.transferMode = transferMode;
    }

    public static String getServerMode() {
        return serverMode;
    }

    public static void setServerMode(String serverMode) {
        ServerContext.serverMode = serverMode;
    }
}
