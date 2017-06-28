package com.riddlesvillage.core.net.http;

import com.google.common.io.Closer;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest implements Closeable {

    private static final int CONNECT_TIMEOUT = 1000 * 5;
    private static final int READ_TIMEOUT = 1000 * 5;
    private static final int READ_BUFFER_SIZE = 1024 * 8;

    private final Map<String, String> headers = new HashMap<>();
    private final String method;
    private final URL url;
    private String contentType;
    private byte[] body;

    private transient HttpURLConnection conn;
    private transient InputStream inputStream;

    /**
     * Create a new HTTP request.
     *
     * @param method the method
     * @param url    the URL
     */
    private HttpRequest(final String method,
                        final URL url) {
        this.method = Validate.notNull(method);
        this.url = Validate.notNull(url);
    }

    /**
     * Submit data.
     *
     * @return this object
     */
    public HttpRequest body(final String data) {
        body = Validate.notNull(data).getBytes();

        return this;
    }

    /**
     * Submit form data.
     *
     * @param form the form
     *
     * @return this object
     */
    public HttpRequest bodyForm(final Form form) {
        Validate.notNull(form);
        contentType = "application/x-www-form-urlencoded";
        body = form.toString().getBytes();

        return this;
    }

    /**
     * Add a header.
     *
     * @param key   the header key
     * @param value the header value
     *
     * @return this object
     */
    public HttpRequest header(final String key,
                              final String value) {
        Validate.notNull(key);
        Validate.notNull(value);
        if (key.equalsIgnoreCase("Content-Type")) {
            contentType = value;
        } else {
            headers.put(key, value);
        }

        return this;
    }

    /**
     * Execute the request.
     * <p>
     * After execution, {@link #close()} should be called.
     *
     * @return this object
     * @throws IOException on I/O error
     */
    public HttpRequest execute() throws IOException {
        boolean successful = false;
        try {
            if (conn != null) {
                throw new IllegalArgumentException("Connection already executed");
            }
            conn = (HttpURLConnection) reformat(url).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Java)");
            if (body != null) {
                conn.setRequestProperty("Content-Type", contentType);
                conn.setRequestProperty("Content-Length", Integer.toString(body.length));
                conn.setDoInput(true);
            }
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.setRequestMethod(method);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.connect();
            if (body != null) {
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(body);
                out.flush();
                out.close();
            }
            inputStream = conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream();
            successful = true;
        } finally {
            if (!successful) {
                close();
            }
        }

        return this;
    }

    /**
     * Require that the response code is one of the given response codes.
     *
     * @param codes a list of codes
     *
     * @return this object
     * @throws IOException if there is an I/O error or the response code is not expected
     */
    public HttpRequest expectResponseCode(final int... codes) throws IOException {
        int responseCode = getResponseCode();
        for (int code : codes) {
            if (code == responseCode) {
                return this;
            }
        }
        close();
        throw new IOException("Did not get expected response code, got " + responseCode + " for " + url);
    }

    /**
     * Get the response code.
     *
     * @return the response code
     * @throws IOException on I/O error
     */
    public int getResponseCode() throws IOException {
        if (conn == null) throw new IOException("No connection has been made");
        return conn.getResponseCode();
    }

    /**
     * Buffer the returned response.
     *
     * @return the buffered response
     * @throws IOException  on I/O error
     * @throws InterruptedException on interruption
     */
    public BufferedResponse returnContent() throws IOException, InterruptedException {
        if (inputStream == null) {
            throw new IllegalArgumentException("No input stream available");
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = inputStream.read()) != -1) {
                bos.write(b);
            }
            return new BufferedResponse(bos.toByteArray());
        } finally {
            close();
        }
    }

    /**
     * Save the result to a file.
     *
     * @param file the file
     *
     * @return this object
     * @throws IOException  on I/O error
     * @throws InterruptedException on interruption
     */
    public HttpRequest saveContent(final File file) throws IOException, InterruptedException {
        Validate.notNull(file);
        try (Closer closer = Closer.create()) {
            FileOutputStream fos = closer.register(new FileOutputStream(file));
            BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
            saveContent(bos);
        }

        return this;
    }

    /**
     * Save the result to an output stream.
     *
     * @param out the output stream
     *
     * @return this object
     * @throws IOException  on I/O error
     * @throws InterruptedException on interruption
     */
    public HttpRequest saveContent(final OutputStream out) throws IOException, InterruptedException {
        Validate.notNull(out);
        BufferedInputStream bis;
        try {
            String field = conn.getHeaderField("Content-Length");
            if (field != null) {
                long len = Long.parseLong(field);
                if (len >= 0) { // Let's just not deal with really big numbers
                    long contentLength = len;
                }
            }
        } catch (NumberFormatException ignored) {
        }
        try {
            bis = new BufferedInputStream(inputStream);
            byte[] data = new byte[READ_BUFFER_SIZE];
            int len;
            while ((len = bis.read(data, 0, READ_BUFFER_SIZE)) >= 0) {
                out.write(data, 0, len);
            }
        } finally {
            close();
        }

        return this;
    }

    @Override
    public void close() throws IOException {
        if (conn != null) conn.disconnect();
    }

    public InputStream getInputStream() {
		return inputStream;
	}

    /**
     * Perform a GET request.
     *
     * @param url the URL
     *
     * @return a new request object
     */
    public static HttpRequest get(final URL url) {
        Validate.notNull(url);
        return request("GET", url);
    }

    /**
     * Perform a POST request.
     *
     * @param url the URL
     *
     * @return a new request object
     */
    public static HttpRequest post(final URL url) {
        Validate.notNull(url);
        return request("POST", url);
    }

    /**
     * Perform a request.
     *
     * @param method the method
     * @param url    the URL
     *
     * @return a new request object
     */
    public static HttpRequest request(final String method,
                                      final URL url) {
        Validate.notNull(method);
        Validate.notNull(url);
        return new HttpRequest(method, url);
    }

    /**
     * Create a new {@link URL} and throw a {@link RuntimeException} if the URL
     * is not valid.
     *
     * @param url the url
     *
     * @return a URL object
     * @throws RuntimeException if the URL is invalid
     */
    public static URL url(final String url) {
        Validate.notNull(url);
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL may contain spaces and other nasties that will cause a failure.
     *
     * @param existing the existing URL to transform
     *
     * @return the new URL, or old one if there was a failure
     */
    private static URL reformat(final URL existing) {
        Validate.notNull(existing);
        try {
            URL url = new URL(existing.toString());
            URI uri = new URI(
                    url.getProtocol(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    url.getQuery(),
                    url.getRef()
            );
            url = uri.toURL();
            return url;
        } catch (MalformedURLException e) {
            return existing;
        } catch (URISyntaxException e) {
            return existing;
        }
    }
}