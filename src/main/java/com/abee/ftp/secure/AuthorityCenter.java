package com.abee.ftp.secure;

import com.abee.ftp.secure.coder.RSACoder;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xincong yao
 */
public class AuthorityCenter extends Thread {

    /**
     * key size: 512 bits.
     * Raw data must not be longer than 53 bytes.
     */
    public static final int DATA_LENGTH = 64;

    public static final String STR_PUBLIC_KEY =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIbQ1h8S47/DrFQ1x+UjES4DpQKl" +
                    "qOER6g4TmYRTGmukwP3CQEmiEAapkuUiL5ALd35rEsVxOA4fhd1THdBl96MCAwEAAQ==";

    public static final String STR_PRIVATE_KEY =
            "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAhtDWHxLjv8OsVD" +
                    "XH5SMRLgOlAqWo4RHqDhOZhFMaa6TA/cJASaIQBqmS5SIvkAt3fmsSxXE4Dh+F3VMd0GX3o" +
                    "wIDAQABAkAHPBvRjRT1zI1p83znugsI+h8X1kYK1ghGexzI8iC/2AJZAtkg/al7+A824KHO" +
                    "kDedmKjRdIAK4adPUAhwCqO5AiEA3S4CmkWDD22dt1wvZrKtySOMTzcBhvIYYRQeTl7/D4c" +
                    "CIQCcCi8mysdBssYvUe5Lbzvk0J+NdzF+WPmEpuXHgOaGBQIhAIrFlm5y1KM2dHEaDseRrg" +
                    "X4Hs2IxpWDihZGyCkF42Q7AiAkR0XhKgIbpznmpJDXnvv43fxKgqzSKFOS4M7JjJGMFQIgS" +
                    "i6AfXWx7z/ly2n1xVTfSfAC8/wV6mtN1piFM7IEe4I=";

    public static final byte[] BYTES_PUBLIC_KEY = Base64.decodeBase64(STR_PUBLIC_KEY);

    public static final byte[] BYTES_PRIVATE_KEY = Base64.decodeBase64(STR_PRIVATE_KEY);

    private String ip;

    private int port;

    private boolean start = true;

    public AuthorityCenter(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));

            System.out.println("FTP Authority Center started. IP: " + serverSocket.getInetAddress() +
                    ", Port: " + serverSocket.getLocalPort());

            while (start) {
                Socket socket = serverSocket.accept();

                byte[] result = authorize(socket);

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(result);
            }
        } catch (IOException e) {
            System.out.println("Certificate Authority start failed.");
            System.out.println(e.getMessage());
        }
    }

    private byte[] authorize(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[DATA_LENGTH];
            inputStream.read(buffer);

            byte[] key = RSACoder.decryptByPrivateKey(buffer, BYTES_PRIVATE_KEY);

            KeyStore.add(socket.getInetAddress().toString(), key);

            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[DATA_LENGTH];
        }
    }

    public void close() {
        start = false;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
