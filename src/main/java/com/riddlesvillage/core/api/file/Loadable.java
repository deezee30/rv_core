package com.riddlesvillage.core.api.file;

import com.riddlesvillage.core.api.file.config.FileConfig;

import java.io.IOException;

/**
 * Created by matt1 on 3/22/2017.
 */
public interface Loadable {

    FileConfig load(String name, String path);

    void unload() throws IOException;
}
