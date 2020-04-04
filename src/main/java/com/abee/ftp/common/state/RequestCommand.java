package com.abee.ftp.common.state;

/**
 * Client request commands.
 *
 * @author xincong yao
 */
public enum RequestCommand {
    /**
     * Print working dictionary
     */
    PWD,
    /**
     * Change working dictionary
     */
    CWD,
    /**
     * Get file information list
     */
    LIST,

    /**
     * Change Server Mode to passive
     */
    PASV,
    /**
     * Change Server Mode to positive
     */
    PORT,

    /**
     * Set transfer type, including ASCII and binary
     */
    TYPE,

    /**
     * Get the size of file
     */
    SIZE,
    /**
     * Download from server
     */
    RETR,
    /**
     * Upload to server
     */
    STOR;
}
