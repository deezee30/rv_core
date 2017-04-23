package com.riddlesvillage.core.api.file;

import java.io.IOException;

/**
 * Created by matt1 on 3/22/2017.
 */
public interface Loadable<L> {

    default L load(String name, String path) {
        return null;
    }

    default L load() {
        return null;
    }

    default void unload() throws IOException {
    }
}
