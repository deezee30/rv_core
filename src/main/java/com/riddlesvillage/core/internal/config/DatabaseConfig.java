/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.database.data.Credentials;

public final class DatabaseConfig extends CoreConfigFile {

    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    public String address;
    public int port;
    public String database;
    public String username;
    public String password;

    private DatabaseConfig() {}

    @Override
    protected String getConfigName() {
        return "database.yml";
    }

    @Override
    protected String[] getPaths() {
        return new String[] {
                "address",
                "port",
                "database",
                "username",
                "password"
        };
    }

    public static Credentials getCredentials() {
        return new Credentials(
                INSTANCE.address,
                INSTANCE.port,
                INSTANCE.database,
                INSTANCE.username,
                INSTANCE.password
        );
    }
}