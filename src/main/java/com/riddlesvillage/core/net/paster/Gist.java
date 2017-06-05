package com.riddlesvillage.core.net.paster;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.net.http.HttpRequest;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Gist extends Paster {

	private static final URL GISTS_API	= HttpRequest.url("https://api.github.com/gists");
	private static final String GISTS	= "https://gist.github.com/anonymous/";

	private final Map<String, Map<String, String>> files = Maps.newHashMap();
	private String description;
	private boolean global = false;

	Gist(Map<String, String> files) {
		super("");
		for (Map.Entry<String, String> entry : files.entrySet()) {
			this.files.put(
					entry.getKey(),
					new ImmutableMap.Builder<String, String>()
							.put("content", entry.getValue())
							.build()
			);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPublic(boolean global) {
		this.global = global;
	}

	@Override
	protected URL process() throws Exception {
		JSONObject json = new JSONObject();
		if (description != null) json.put("description", description);
		json.put("public", global);
		json.put("files", files);

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) GISTS_API.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.write(json.toJSONString().getBytes(StandardCharsets.UTF_8));
				wr.flush();
			}

			StringBuilder sb = new StringBuilder();

			try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line);
				}
			}

			Map<String, Object> map = EnhancedMap.fromJson(sb.toString());

			int responseCode = connection.getResponseCode();
			if (responseCode != 201) {
				throw new PasteException(String.format(
						"Response code returned is not 201, received %s instead. Error: %s",
						responseCode,
						map.get("message")
				));
			}

			return HttpRequest.url(GISTS + map.get("id").toString());
		} finally {
			if (connection != null) connection.disconnect();
		}
	}
}