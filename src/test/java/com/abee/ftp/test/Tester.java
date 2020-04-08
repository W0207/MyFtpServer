package com.abee.ftp.test;

import java.io.*;

public class Tester {
    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 10000 - i * 10; j++) {
                stringBuilder.append(i);
            }
            FileWriter fr = new FileWriter(stringBuilder.toString(),
                    "D:/OTHER/temp/4-8-test.txt",
                    "Thread-" + i);
            fr.start();
        }
    }
}
