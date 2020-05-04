package com.abee.ftp.client.secure;

import com.abee.ftp.secure.coder.AESCoder;
import com.abee.ftp.secure.coder.RSACoder;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author xincong yao
 */
public class Authenticator {

    public static final int DATA_LENGTH = 64;

    public static final String STR_PUBLIC_KEY =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIbQ1h8S47/DrFQ1x+UjES4DpQKl" +
                    "qOER6g4TmYRTGmukwP3CQEmiEAapkuUiL5ALd35rEsVxOA4fhd1THdBl96MCAwEAAQ==";

    public static final byte[] BYTES_PUBLIC_KEY = Base64.decodeBase64(STR_PUBLIC_KEY);

    public static final byte[] LOCAL_PUBLIC_KEY = new AESCoder().getByteKey();

    private Socket socket;

    public boolean connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate() {
        if (!socket.isConnected()) {
            System.out.println("Connect certificate authority first.");
            return false;
        }

        try {
            byte[] data = RSACoder.encryptByPublicKey(LOCAL_PUBLIC_KEY, BYTES_PUBLIC_KEY);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);

            InputStream inputStream = socket.getInputStream();
            byte[] data2 = new byte[DATA_LENGTH];
            inputStream.read(data2);

            if (Arrays.equals(data, data2)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
