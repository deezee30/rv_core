/*
 * MySQLLib
 *
 * Created on 22 July 2014 at 10:57 AM.
 */

package com.riddlesvillage.core.database.data;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public final class Credentials implements ConfigurationSerializable {

	private final String address;
	private final String database;
	private final String user;
	private final String pass;
	private final int port;

	public Credentials(String address,
					   String database,
					   String user,
					   String pass,
					   int port) {
		this.address  = Validate.notNull(address);
		this.database = Validate.notNull(database);
		this.user = Validate.notNull(user);
		this.pass = Validate.notNull(pass);
		this.port = Validate.notNull(port);
	}

	public Credentials(Map<String, Object> data) {
		this(
				(String) data.get("address"),
				(String) data.get("database"),
				(String) data.get("username"),
				(String) data.get("password"),
				(Integer) data.get("port")
		);
	}

	/**
	 * @return	The address including the hostname and port (and perhaps properties) of the
	 * 			database server.
	 */
	public synchronized String getAddress() {
		return address;
	}

	/**
	 * @return The database name of the database server.
	 */
	public synchronized String getDatabase() {
		return database;
	}

	/**
	 * @return The username of the database server.
	 */
	public synchronized String getUser() {
		return user;
	}

	public int getPort() {
		return port;
	}

	/**
	 * @return The password of the database server.
	 */
	public synchronized String getPass() {
		return pass;
	}

	@Override
	public Map<String, Object> serialize() {
		return new ImmutableMap.Builder<String, Object>()
				.put("address",		address)
				.put("database", 	database)
				.put("username",	user)
				.put("password",	pass)
				.put("port",	port)
				.build();
	}
}