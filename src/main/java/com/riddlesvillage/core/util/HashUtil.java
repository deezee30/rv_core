package com.riddlesvillage.core.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

	private static final String        DEFAULT_ENCODING = "UTF-8";
	private static final BASE64Encoder ENCODER          = new BASE64Encoder();
	private static final BASE64Decoder DECODER          = new BASE64Decoder();

	/* Disable initialization */
	private HashUtil() {}

	public static String toBase64(Object obj) {
		try {
			return ENCODER.encode(obj.toString().getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String fromBase64(Object obj) {
		try {
			return new String(DECODER.decodeBuffer(obj.toString()), DEFAULT_ENCODING);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * The fastest way possible to hash a {@param a} with a
	 * {@code MD5} algorithm.  Usually takes about {@code 0}
	 * to {@code 1} milliseconds.  The hashed String has a
	 * length of 32 characters with no dashes.
	 *
	 * @param	a The String to be serialized.
	 * @return	The hashed String in length of 32 characters,
	 * 			with no dashes.
	 */
	public static String hash(String a) {
		try {

			MessageDigest b = MessageDigest.getInstance("MD5");
			byte[] c = b.digest(a.getBytes());
			StringBuilder d = new StringBuilder();

			for (byte e : c)
				d.append(Integer.toHexString((e & 0xFF) | 0x100).substring(1, 3));

			return d.toString();
		} catch (NoSuchAlgorithmException ignored) {}
		return "";
	}

	public static String toBase64(ItemStack[] items) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(items.length);

			// Save every element in the list
			for (ItemStack item : items) {
				dataOutput.writeObject(item);
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks", e);
		}
	}

	public static String toBase64(Inventory inv) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inv.getContents().length);

			// Save every element in the list
			for (int i = 0; i < inv.getContents().length; i++) {
				dataOutput.writeObject(inv.getContents()[i]);
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	public static ItemStack[] fromItemsBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];

			// Read the serialized inventory
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}
			dataInput.close();
			return items;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

	public static Inventory fromBase64(String data, String title) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), title);

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}
			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

	public static Inventory fromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}
			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
}