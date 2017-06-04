package com.riddlesvillage.core.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

	// Disable initialization
	private StringUtil() {}

	/**
	 * Removes a specific section of the {@param origin} string
	 * by replacing that section with an empty string.
	 *
	 * <p>  Neither of the {@code String}s used for parameters can
	 * take formatting regex.  {@code String}s must be plain.</p>
	 *
	 * @param 	origin The super string that needs to be modofied.
	 * @param 	sequence The section of {@param origin} that needs to be removed.
	 * @return 	The new string with {@param sequence} removed.
	 * @see		String#replace(CharSequence, CharSequence)
	 */
	public static String remove(String origin, String sequence) {
		return Validate.notNull(origin, "Cannot remove sequence from null string")
				.replace(Validate.notNull(sequence, "Cannot remove null sequence from string"), "");
	}

	/**
	 * This method uses a region to check case-insensitive equality.
	 * This means the internal array does not need to be copied
	 * like a {@code toLowerCase()} call would.
	 *
	 * @param 	string
	 * 			String to check.
	 * @param 	prefix
	 * 			Prefix of string to compare.
	 * @return	true if provided string starts with,
	 * 			ignoring case, the prefix provided.
	 * @throws  NullPointerException
	 * 			If prefix is null.
	 * @throws  IllegalArgumentException
	 * 			If string is null.
	 * @see		String#regionMatches(boolean, int, String, int, int)
	 */
	public static boolean startsWithIgnoreCase(final String string, final String prefix)
			throws IllegalArgumentException, NullPointerException {
		return Validate.notNull(string, "Cannot check a null string for a match").length()
				>= prefix.length()
				&& string.regionMatches(true, 0, prefix, 0, prefix.length());
	}

	public static String checkPlural(String singular, String plural, int count) {
		return count == 1 ? singular : plural;
	}

	public static ImmutableList<String> splitMaintainBounds(String longString, int maxLength) {
		final Matcher m = Pattern.compile("\\G\\s*(.{1," + maxLength + "})(?=\\s|$)", Pattern.DOTALL).matcher(longString);
		final ImmutableList.Builder<String> strings = new ImmutableList.Builder<>();
		while (m.find()) strings.add(m.group(1));

		return strings.build();
	}
}