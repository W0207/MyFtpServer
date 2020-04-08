package com.abee.ftp.common.state;

/**
 * Client request commands.
 *
 * @author xincong yao
 */
public enum RequestCommand {
    /**
     * Print working directory
     */
    PWD,
    /**
     * Change working directory
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
    STOR,

    /**
     * Generate directory
     */
    MKD;
}
