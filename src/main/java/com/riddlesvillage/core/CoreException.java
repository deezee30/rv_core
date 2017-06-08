/*
 * rv_core
 * 
 * Created on 03 June 2017 at 7:02 PM.
 */

package com.riddlesvillage.core;

import org.apache.commons.lang3.Validate;

import java.net.URL;
import java.util.Optional;

public class CoreException extends Exception {

	private static final long serialVersionUID = -6450641089472167745L;

	private final Optional<URL> pasteLink = Optional.empty();

	public CoreException(String message,
						 Object... components) {
		super(Messaging.buildMessage(Validate.notNull(message), components));
	}

	public CoreException(String message,
						 Throwable cause) {
		super(Validate.notNull(message), Validate.notNull(cause));
	}

	public CoreException(Throwable cause) {
		super(Validate.notNull(cause));
	}

	public CoreException(String message,
						 Throwable cause,
						 boolean enableSuppression,
						 boolean writableStackTrace) {
		super(
				Validate.notNull(message),
				Validate.notNull(cause),
				enableSuppression,
				writableStackTrace
		);
	}

	/*
	@Override
	public void printStackTrace() {
		Console console = System.console();
		if (console == null) {
			if (getCause() == null) super.printStackTrace();
			else 					getCause().printStackTrace();
			return;
		}

		String stacktrace = ExceptionUtils.getStackTrace(this);

		try {
			synchronized (this) {
				ListenableFuture<URL> pasteUrl = PASTER.paste(stacktrace);
				pasteLink = Optional.of(pasteUrl.get());

				StringBuilder msg = new StringBuilder();
				for (String part : splitIfTooLong(toString(), 54)) {
					msg.append("\n\t\t   ");
					msg.append(part);
				}

				Messaging.log(
						"%s%s%s:\n\t\t   %s",
						Messaging.getNoPrefixChar(),
						Messaging.BORDER,
						msg.toString(),
						pasteLink.get()
				);
			}
		} catch (InterruptedException | ExecutionException e) {
			Messaging.log(
					"%s%s: %s",
					Messaging.getNoPrefixChar(),
					e,
					e.getMessage()
			);
			Thread.currentThread().interrupt();
		} finally {
			Messaging.log(Messaging.getNoPrefixChar() + Messaging.BORDER);
		}
	}
	*/

	public Optional<URL> getPasteLink() {
		return pasteLink;
	}

	private String[] splitIfTooLong(String origin, int sectionLength) {
		int length = origin.length();
		String prototype = origin;
		String[] parts = new String[(length / sectionLength) + 1];
		int x = 0;
		while (prototype.length() > sectionLength) {
			parts[x] = prototype.substring(0, sectionLength);
			prototype = prototype.substring(sectionLength);
			++x;
		}

		int remainingChars = length % sectionLength;
		parts[parts.length - 1] = origin.substring(length - remainingChars);

		return parts;
	}
}