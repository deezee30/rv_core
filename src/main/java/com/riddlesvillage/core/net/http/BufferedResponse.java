package com.riddlesvillage.core.net.http;

import com.google.common.io.Closer;
import org.apache.commons.lang3.Validate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * Used to buffer the response in memory.
 */
public final class BufferedResponse {

    private final byte[] data;

    BufferedResponse(final byte[] data) {
        this.data = Validate.notNull(data);
    }

    /**
     * Return the result as bytes.
     *
     * @return the data
     */
    public byte[] asBytes() {
        return data;
    }

    /**
     * Return the result as a string.
     *
     * @param 	encoding
     * 			The encoding.
     * @return 	New String with new encoding.
     * @throws 	IOException
     * 			If I/O fails.
     * @throws	UnsupportedEncodingException
     * 			If the named charset is not supported (or {@code null}).
     */
    public String asString(final String encoding) throws IOException {
        return new String(data, Validate.notNull(encoding));
    }

    /**
     * Return the result as an instance of the given class that has been
     * deserialized from a XML payload.
     *
     * @return the object
     * @throws IOException on I/O error
     */
    @SuppressWarnings ("unchecked")
    public <T> T asXml(final Class<T> cls) throws IOException {
        Validate.notNull(cls);
        try {
            JAXBContext context = JAXBContext.newInstance(cls);
            Unmarshaller um = context.createUnmarshaller();
            return (T) um.unmarshal(new ByteArrayInputStream(data));
        } catch (JAXBException e) {
            throw new IOException(e);
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
    public BufferedResponse saveContent(final File file) throws IOException, InterruptedException {
        Validate.notNull(file);
        file.getParentFile().mkdirs();
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
    public BufferedResponse saveContent(final OutputStream out) throws IOException, InterruptedException {
        Validate.notNull(out);
        out.write(data);
        return this;
    }
}