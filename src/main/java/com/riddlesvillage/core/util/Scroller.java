/*
 * rv_core
 * 
 * Created on 11 June 2017 at 7:49 PM.
 */

package com.riddlesvillage.core.util;

import java.io.Serializable;
import java.util.LinkedList;

public final class Scroller implements Serializable {

	private static final long serialVersionUID = -3296965334328559328L;

	private int position = 0;
	private final LinkedList<String> list = new LinkedList<>();

	/**
	 * @param message      The String to scroll
	 * @param width        The width of the window to scroll across
	 * @param spaceBetween The amount of spaces between each repetition
	 */
	public Scroller(String message,
					int width,
					int spaceBetween) {
		if (message.length() < width) {
			StringBuilder sb = new StringBuilder(message);
			while (sb.length() < width) sb.append(" ");
			message = sb.toString();
		}

		width -= 2;

		if (width < 1) width = 1;
		if (spaceBetween < 0) spaceBetween = 0;


		for (int i = 0; i < message.length() - width; i++)
			list.add(message.substring(i, i + width));

		StringBuilder space = new StringBuilder();
		for (int i = 0; i < spaceBetween; i++) {
			list.add(message.substring(message.length() - width + (i > width ? width : i), message.length()) + space);
			if (space.length() < width) space.append(" ");
		}

		for (int i = 0; i < width - spaceBetween; ++i)
			list.add(message.substring(message.length() - width + spaceBetween + i, message.length()) + space + message.substring(0, i));

		for (int i = 0; i < spaceBetween; i++) {
			if (i > space.length()) break;
			list.add(space.substring(0, space.length() - i) + message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
		}
	}

	/**
	 * @return The next String to display
	 */
	public String next() {
		return list.get(position++ % list.size());
	}
}