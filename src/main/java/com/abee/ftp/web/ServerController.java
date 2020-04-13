package com.abee.ftp.web;

import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.server.MyFtpServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.UnknownHostException;

/**
 * @author xincong yao
 */
@RestController
@RequestMapping("server")
public class ServerController {

    @RequestMapping
    public ModelAndView index() {
        return new ModelAndView("server.html");
    }

    @RequestMapping("start")
    public String start(String ip, Integer port) {
        MyFtpServer server = new MyFtpServer();

        try {
            ServerCommandListener serverCommandListener = new ServerCommandListener(ip, port);
            server.setCommandListener(serverCommandListener);
        } catch (UnknownHostException e) {
            return "Server start failed.";
        }

        server.start();

        return "Server started " + ip + ":" + port;
    }
}
