package com.github.eikefab.libs.pluginupdater;

import java.io.File;
import java.util.LinkedList;

/**
 * Representation of a release on GitHub
 */
public class Release {

    private final int id;
    private final String body;
    private final String version;
    private final boolean preRelease;
    private final String url;
    private final LinkedList<Asset> assets;

    public Release(int id, String body, String version, boolean preRelease, String url, LinkedList<Asset> assets) {
        this.id = id;
        this.body = body;
        this.version = version;
        this.preRelease = preRelease;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public LinkedList<Asset> getAssets() {
        return assets;
    }

    public void download(String token, File folder) {
        for (Asset asset : assets) {
            asset.download(token, folder);
        }
    }

    public void download(File folder) {
        download(null, folder);
    }

}
