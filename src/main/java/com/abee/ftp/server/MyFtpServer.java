package com.abee.ftp.server;

import com.abee.ftp.common.listener.CommandListener;
import com.abee.ftp.config.ServerContext;
import com.abee.ftp.secure.AuthorityCenter;

/**
 * @author xincong yao
 */
public class MyFtpServer {

    /**
     * todo: It could be a List, not only one single listener.
     */
    private CommandListener commandListener;

    private AuthorityCenter authorityCenter;

    public void start() {
        if (commandListener != null) {
            commandListener.start();
            authorityCenter.start();
        }
    }

    public boolean setRoot(String root) {
        return ServerContext.setRoot(root);
    }

    public void setCommandListener(CommandListener listener) {
        commandListener = listener;
    }

    public void setAuthorityCenter(AuthorityCenter c) {
        this.authorityCenter = c;
    }

    @Override
    public int hashCode() {
        return (commandListener.getSocketAddress().toString() + authorityCenter.toString()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "Listener: " + commandListener.getSocketAddress().toString() +
                ", Authority Center: " + authorityCenter.toString();
    }

    public void close() {
        if (commandListener != null) {
            commandListener.close();
        }

        if (authorityCenter != null) {
            authorityCenter.close();
        }
    }
}
