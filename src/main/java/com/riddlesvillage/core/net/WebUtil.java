package com.riddlesvillage.core.net;

import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.net.http.Form;
import com.riddlesvillage.core.service.ServiceExecutor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class WebUtil {

	/* Disable initialization */
	private WebUtil() {}


	public static Form mozillaForm() {
		return Form.create().add("User-Agent", "Mozilla/5.0");
	}

	public static void ping(String url) {
		try {
			ping(new URL(url));
		} catch (MalformedURLException e) {
			try {
				throw new CoreException(e);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void ping(final URL url) {
		ServiceExecutor.getCachedExecutor().execute(() -> {
			try {
				url.openStream().close();
			} catch (IOException e) {
				try {
					throw new CoreException(e);
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}