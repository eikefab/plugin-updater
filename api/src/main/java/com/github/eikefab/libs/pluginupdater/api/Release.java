package com.github.eikefab.libs.pluginupdater.api;

import java.util.LinkedList;

public class Release {

    private final int id;
    private final String body;
    private final String version;
    private final boolean preRelease;
    private final LinkedList<Asset> assets;

    public Release(int id, String body, String version, boolean preRelease, LinkedList<Asset> assets) {
        this.id = id;
        this.body = body;
        this.version = version;
        this.preRelease = preRelease;
        this.assets = assets;
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getVersion() {
        return version;
    }

    public boolean isPreRelease() {
        return preRelease;
    }

    public LinkedList<Asset> getAssets() {
        return assets;
    }

}
