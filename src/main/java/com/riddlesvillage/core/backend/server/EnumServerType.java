package com.riddlesvillage.core.backend.server;

/**
 * Created by Matthew E on 6/11/2017.
 */
public enum EnumServerType {
    LOBBY(0, "Lobby"),
    GAME(1, "Game"),
    DEVELOPMENT(2, "Development");

    private int id;
    private String name;

    EnumServerType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
