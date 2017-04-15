package com.riddlesvillage.core.database.data;

public enum EnumData {

    UUID("info.uuid"),
    USERNAME("info.username"),
    USERNAME_HISTORY("info.usernameHistory"),
    FIRST_LOGIN("info.firstLogin"),
    LAST_LOGIN("info.lastLogin"),
    LAST_LOGOUT("info.lastLogout"),
    IP_ADDRESS("info.ipAddress"),
    IS_PLAYING("info.isPlaying"),
    RANK("info.rank"),
    IP_ADDRESS_HISTORY("info.ipAddressHistory"),
    IS_NEW("info.isNew");

    private String key;

    EnumData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
