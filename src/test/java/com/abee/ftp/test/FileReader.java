package com.abee.ftp.test;

import com.abee.ftp.common.tool.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReader extends Thread {

    private String result;

    private String uri;

    public FileReader(String result, String uri) {
        this.result = result;
        this.uri = uri;
    }

    @Override
    public void run() {
        System.out.println(FileUtil.write(result.getBytes(), uri));
    }
}
