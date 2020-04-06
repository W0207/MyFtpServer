package com.abee.ftp.test;

import java.io.File;

public class Tester {
    public static void main(String[] args) {
        String uri = "D:/OTHER/multi-thread-test.txt";
        for (int t = 0; t < 50; t++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000 - t; i++) {
                sb.append(t);
            }
            String result = sb.toString();
            FileReader fr = new FileReader(result, uri);
            fr.start();
        }
    }
}
