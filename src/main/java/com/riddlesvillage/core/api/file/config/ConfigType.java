package com.riddlesvillage.core.api.file.config;

/**
 * Created by matt1 on 3/22/2017.
 */
public enum ConfigType {
    JSON(0, "Json", ".json"),
    YAML(1, "Yaml", ".yml");

    private int id;
    private String name;
    private String fileExtension;

    ConfigType(int id, String name, String fileExtension) {
        this.id = id;
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
