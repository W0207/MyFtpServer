package com.abee.ftp.client;

import com.abee.ftp.common.state.ResponseBody;
import com.abee.ftp.common.state.ResponseCode;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Objects;

public class AdvancedOperationSet extends BasicOperationSet {

    public AdvancedOperationSet(ObjectOutputStream out, ObjectInputStream in) {
        super(out, in);
    }

    /**
     * @param file Local file, which could be a folder or a common file.
     */
    public boolean uploads(File file) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            for (File f: Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    mkd(f.getName());
                    String pwd = pwd().getArg();
                    cwd(pwd + "/" + f.getName());
                    uploads(f);
                    cwd(pwd);
                } else {
                    /**
                     * Get a passive port from server
                     */
                    ResponseBody response = pasv();
                    /**
                     * Preparation before real data transfer.
                     */
                    stor(f);
                    /**
                     * Transfer data.
                     */
                    upload(f, response.getPassivePort());
                }
            }
        } else {
            ResponseBody response = pasv();
            stor(file);
            upload(file, response.getPassivePort());
        }

        return true;
    }

    /**
     * @param file    Local file to store remote file.
     * @param remote  Remote directory of target file.
     */
    public boolean downloads(File file, String remote) throws IOException, ClassNotFoundException {
        /**
         * if change working directory operation success
         */
        if (cwd(remote).getCode().equals(ResponseCode._250)) {
            Map<String, Boolean> files = list();

            for (String key: files.keySet()) {
                /**
                 * if (isDirectory) ...
                 */
                if (files.get(key)) {
                    File subDirectory = new File(file.getPath() + "/" + key);
                    subDirectory.mkdir();
                    downloads(subDirectory, remote + "/" + key);
                    cwd(remote);
                } else {
                    /**
                     * Get a passive port from server
                     */
                    ResponseBody response = pasv();

                    retr(key);

                    File f = new File(file.getPath() + "/" + key);
                    download(f, response.getPassivePort());
                }
            }
        } else {
            int tag = remote.lastIndexOf("/");

            cwd(remote.substring(0, tag));

            ResponseBody response = pasv();

            retr(remote.substring(tag));

            File f = new File(file.getPath() + "/" + remote.substring(tag));
            download(f, response.getPassivePort());
        }


        return true;
    }
}
