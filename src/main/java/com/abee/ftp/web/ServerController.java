package com.abee.ftp.web;

import com.abee.ftp.common.listener.ServerCommandListener;
import com.abee.ftp.config.ServerContext;
import com.abee.ftp.secure.AuthorityCenter;
import com.abee.ftp.server.MyFtpServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xincong yao
 */
@RestController
@RequestMapping("server")
public class ServerController {

    private Set<MyFtpServer> servers = new HashSet<>(4);

    @RequestMapping
    public ModelAndView index() {
        return new ModelAndView("server.html");
    }

    @RequestMapping("start")
    public String start(String ip, Integer lPort, Integer sPort, String root) {
        MyFtpServer server = new MyFtpServer();

        try {
            ServerCommandListener serverCommandListener = new ServerCommandListener(ip, lPort);
            server.setCommandListener(serverCommandListener);

            AuthorityCenter certificateAuthority = new AuthorityCenter(ip, sPort);
            server.setAuthorityCenter(certificateAuthority);
        } catch (UnknownHostException e) {
            return "Server start failed.";
        }

        if (!servers.contains(server)) {
            servers.add(server);
        } else {
            return "Server with ip: " + ip +
                    ", listener port: " + lPort +
                    ", secure port: " + sPort +
                    " already started.";
        }

        if (server.setRoot(root)) {
            server.start();
        } else {
            return "Root '" + root + "' not exist.";
        }

        return "Server started";
    }

    @RequestMapping("shutdown")
    public String shutdown(String ip, Integer lPort, Integer sPort) {
        MyFtpServer server = new MyFtpServer();

        try {
            ServerCommandListener serverCommandListener = new ServerCommandListener(ip, lPort);
            server.setCommandListener(serverCommandListener);

            AuthorityCenter certificateAuthority = new AuthorityCenter(ip, sPort);
            server.setAuthorityCenter(certificateAuthority);
        } catch (UnknownHostException e) {
            return "UnknownHost:" + ip;
        }

        for (MyFtpServer ms: servers) {
            if (server.equals(ms)) {
                ms.close();
                servers.remove(ms);
                return "Server with ip: " + ip +
                        ", listener port: " + lPort +
                        ", secure port: " + sPort +
                        " closed.";
            }
        }

        return "Server with ip: " + ip +
                ", listener port: " + lPort +
                ", secure port: " + sPort +
                " not found.";
    }

    @RequestMapping("servers")
    public String servers() {
        StringBuilder sb = new StringBuilder();
        for (MyFtpServer server: servers) {
            sb.append(server.toString()).append("<br>");
        }
        return sb.toString();
    }

    @RequestMapping("root")
    public String root(String ip, Integer lPort, Integer sPort) {
        MyFtpServer server = new MyFtpServer();

        try {
            ServerCommandListener serverCommandListener = new ServerCommandListener(ip, lPort);
            server.setCommandListener(serverCommandListener);

            AuthorityCenter certificateAuthority = new AuthorityCenter(ip, sPort);
            server.setAuthorityCenter(certificateAuthority);
        } catch (UnknownHostException e) {
            return "UnknownHost:" + ip;
        }

        for (MyFtpServer ms: servers) {
            if (server.equals(ms)) {
                return ms.getRoot();
            }
        }

        return "Server not found.";
    }
}
