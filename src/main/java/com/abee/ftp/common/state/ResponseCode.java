package com.abee.ftp.common.state;

/**
 * FTP Server response code with argument(description).
 *
 * @author xincong yao
 */

public enum ResponseCode {
    /**
     * Ready to accept new users, response to new connection.
     */
    _220(220, "Server ready for new users"),

    /**
     * Response to command: PWD, MKD
     */
    _257(257, "Current dictionary"),

    /**
     * Dictionary changed, response to command: CWD
     */
    _250(250, "Dictionary changed"),

    /**
     * Turn to passive mode, response to command: PASV
     */
    _227(227, "Entering passive mode"),

    /**
     * Response to SIZE commonly.
     */
    _213(213, "File status"),
    /**
     * Open ASCII or binary transfer
     */
    _150(150, "Open transfer connection with mode"),

    /**
     * Transfer complete
     */
    _226(226, "Transfer complete"),

    /**
     * Close connection
     */
    _221(221, "Connection closed"),

    /**
     * Failed
     */
    _500(500, "Failed"),

    /**
     * Operation success.
     */
    _200(200, "Command okay");


    /**
     * code of response states
     */
    public int code;
    /**
     * description of response states
     */
    public String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
