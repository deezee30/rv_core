package com.riddlesvillage.core.backend.server;

/**
 * Created by Matthew E on 6/11/2017.
 */
public class RiddlesServer {
    private String name;
    private String ipAddress;
    private int port;
    private EnumServerType serverType;

    public RiddlesServer(String name, String ipAddress, int port, EnumServerType serverType) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.serverType = serverType;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public EnumServerType getServerType() {
        return serverType;
    }
}
