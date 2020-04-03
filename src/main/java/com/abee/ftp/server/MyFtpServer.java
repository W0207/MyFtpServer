package com.abee.ftp.server;

import com.abee.ftp.common.listener.CommandListener;

/**
 * @author xincong yao
 */
public class MyFtpServer {

    /**
     * todo: It could be a List, not only one single listener.
     */
    private CommandListener commandListener;

    public void start() {
        if (commandListener != null) {
            commandListener.start();
        }
    }

    public void setCommandListener(CommandListener listener) {
        commandListener = listener;
    }
}
