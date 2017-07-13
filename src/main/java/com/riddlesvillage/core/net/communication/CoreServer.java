/*
 * rv_core
 * 
 * Created on 10 July 2017 at 8:29 PM.
 */

package com.riddlesvillage.core.net.communication;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;

public class CoreServer implements Serializable {

    private static final long serialVersionUID = -2400216533348037042L;

    private final String internalName;
    private final String address;
    private final int port;
    private final long init = System.currentTimeMillis();
    private final String command;

    public CoreServer(final String internalName,
                      final String address,
                      final String command) {
        this(internalName, address, 25565, command);
    }

    public CoreServer(final String internalName,
                      final String address,
                      final int port,
                      final String command) {
        this.internalName = Validate.notNull(internalName);
        this.address = Validate.notNull(address);
        this.port = port;
        this.command = Validate.notNull(command);
    }

    public String getInternalName() {
        return internalName;
    }

    public String getFriendlyName() {
        return WordUtils.capitalize(internalName.replace("-", " ").replace("_", " "));
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public long getTimeReceived() {
        return init;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CoreServer that = (CoreServer) o;

        return new EqualsBuilder()
                .append(port, that.port)
                .append(init, that.init)
                .append(internalName, that.internalName)
                .append(address, that.address)
                .append(command, that.command)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(internalName)
                .append(address)
                .append(port)
                .append(init)
                .append(command)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("internalName", internalName)
                .append("address", address)
                .append("port", port)
                .append("init", init)
                .append("command", command)
                .toString();
    }
}