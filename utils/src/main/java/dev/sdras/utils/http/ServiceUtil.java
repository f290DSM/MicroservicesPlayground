package dev.sdras.utils.http;

import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServiceUtil implements ApplicationListener<WebServerInitializedEvent> {
    private Integer serverPort;
    private String serverIp;

    public Integer getServerPort() {
        return serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getServerAddress() {
        return "http://" + serverIp + ":" + serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
        try {
            this.serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            this.serverIp = "unknown";
        }
    }
}


