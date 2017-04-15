package com.riddlesvillage.core.api.file;

import java.io.IOException;

/**
 * Created by matt1 on 3/22/2017.
 */
public interface FileOperations {
    void delete() throws IOException;
    void save() throws IOException;
    void rename(String newName);
    void create();
}
