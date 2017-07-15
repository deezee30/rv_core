/*
 * MySQLLib
 *
 * Created on 22 July 2014 at 10:57 AM.
 */

package com.riddlesvillage.core.database.data;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public final class Credentials implements ConfigurationSerializable {

    private static final String CHANGE_ME = "CHANGE_ME";

    private final String address;
    private final String database;
    private final String user;
    private final String pass;
    private boolean set = false;

    public Credentials(final String address,
                       final String database,
                       final String user,
                       final String pass) {
        this.address  = Validate.notNull(address);
        this.database = Validate.notNull(database);
        this.user = Validate.notNull(user);
        this.pass = Validate.notNull(pass);

        set = !address.equals(CHANGE_ME)
                && !database.equals(CHANGE_ME)
                && !user.equals(CHANGE_ME)
                && !pass.equals(CHANGE_ME);
    }

    public Credentials(final Map<String, Object> data) {
        this(
                (String) data.get("address"),
                (String) data.get("database"),
                (String) data.get("username"),
                (String) data.get("password")
        );
    }

    public String getAddress() {
        return address;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public boolean isSet() {
        return set;
    }

    @Override
    public Map<String, Object> serialize() {
        return new ImmutableMap.Builder<String, Object>()
                .put("address", address)
                .put("database", database)
                .put("username", user)
                .put("password", pass)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("address", address)
                .append("database", database)
                .append("user", user)
                .append("pass", pass)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Credentials that = (Credentials) o;

        return new EqualsBuilder()
                .append(address, that.address)
                .append(database, that.database)
                .append(user, that.user)
                .append(pass, that.pass)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(address)
                .append(database)
                .append(user)
                .append(pass)
                .toHashCode();
    }
}