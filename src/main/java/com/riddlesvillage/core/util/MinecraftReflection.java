/*
 * RiddlesCore
 */

package com.riddlesvillage.core.util;

import com.riddlesvillage.core.Core;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A class containing static utility methods and caches which are
 * intended as reflective conveniences.  Unless otherwise noted,
 * upon failure methods will return {@code null}.
 */
public final class MinecraftReflection {

	/**
	 * Stores loaded classes from the {@code net.minecraft.server} package.
	 */
	private static final Map<String, Class<?>> loadedNMSClasses = new HashMap<>();

	/**
	 * Stores loaded classes from the {@code org.bukkit.craftbukkit} package
	 * (and subpackages).
	 */
	private static final Map<String, Class<?>> loadedOBCClasses = new HashMap<>();

	private static String versionString;

	/* Disable initialization */
	private MinecraftReflection() {}

	/**
	 * Gets the version string from the package name of the CraftBukkit server
	 * implementation.  This is needed to bypass the JAR package name changing
	 * on each update.
	 *
	 * @return 	The version string of the OBC and NMS packages,
	 * 			<em>including the trailing dot</em>.
	 */
	public synchronized static String getVersion() {
		if (versionString == null) {
			if (Core.get().getServer() == null) {
				// The server hasn't started, static initializer call?
				return null;
			}
			String name = Core.get().getServer().getClass().getPackage().getName();
			versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
		}

		return versionString;
	}

	/**
	 * Gets a {@link Class} object representing a type contained within the {@code
	 * net.minecraft.server} versioned package. The class instances returned by
	 * this method are cached, such that no lookup will be done twice (unless
	 * multiple threads are accessing this method simultaneously).
	 *
	 * @param 	className
	 * 			The name of the class, excluding the package, within NMS.
	 * @return 	The class instance representing the specified NMS class, or
	 * 			{@code null}if it could not be loaded.
	 */
	public synchronized static Class<?> getNMSClass(String className) {
		if (loadedNMSClasses.containsKey(className)) {
			return loadedNMSClasses.get(className);
		}

		String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
			loadedNMSClasses.put(className, null);
			return null;
		}
		loadedNMSClasses.put(className, clazz);
		return clazz;
	}

	/**
	 * Gets a {@link Class} object representing a type contained within the {@code
	 * org.bukkit.craftbukkit} versioned package.  The class instances returned by
	 * this method are cached, such that no lookup will be done twice (unless
	 * multiple threads are accessing this method simultaneously).
	 *
	 * @param 	className
	 * 			The name of the class, excluding the package, within OBC. This name
	 * 			may contain a subpackage name, such as {@code inventory.CraftItemStack}.
	 * @return 	The class instance representing the specified OBC class, or {@code null}
	 * 			if it could not be loaded.
	 */
	public synchronized static Class<?> getOBCClass(String className) {
		if (loadedOBCClasses.containsKey(className)) {
			return loadedOBCClasses.get(className);
		}

		String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
		Class<?> clazz;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
			loadedOBCClasses.put(className, null);
			return null;
		}
		loadedOBCClasses.put(className, clazz);
		return clazz;
	}

	public static void sendPacket(Player p, Object packet) {
		try {
			Object nmsPlayer = Reflection.getHandle(p);
			Field con_field = nmsPlayer.getClass().getField("playerConnection");
			Object con = con_field.get(nmsPlayer);
			Method packet_method = Reflection.getMethod(con.getClass(), "sendPacket", getNMSClass("Packet"));
			packet_method.invoke(con, packet);
		} catch (SecurityException
				| IllegalArgumentException
				| IllegalAccessException
				| NoSuchFieldException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}