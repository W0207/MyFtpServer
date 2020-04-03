package com.abee.ftp.common.state;

import java.io.Serializable;

/**
 * Client request body, including command and arguments(optional).
 *
 * @author xincong yao
 */
public class RequestBody implements Serializable {

    private RequestCommand command;

    private String arg;

    public RequestBody(RequestCommand command) {
        this.command = command;
    }

    public RequestBody(RequestCommand command, String arg) {
        this.command = command;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "command=" + command +
                ", arg='" + arg + '\'' +
                '}';
    }

    public RequestCommand getCommand() {
        return command;
    }

    public String getArg() {
        return arg;
    }

    public void setCommand(RequestCommand command) {
        this.command = command;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }
}
