package com.github.eikefab.libs.pluginupdater;

public class Asset {

    private final int id;
    private final String name;
    private final String downloadUrl;
    private final String contentType;

    public Asset(int id, String name, String downloadUrl, String contentType) {
        this.id = id;
        this.name = name;
        this.downloadUrl = downloadUrl;
        this.contentType = contentType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getContentType() {
        return contentType;
    }

}
