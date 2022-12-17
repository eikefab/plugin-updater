package com.github.eikefab.libs.pluginupdater.downloader;

import com.github.eikefab.libs.pluginupdater.Asset;
import com.github.eikefab.libs.pluginupdater.Release;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class Downloader {

    private final OkHttpClient client;
    private final Release release;
    private final String token;

    public Downloader(Release release, String token) {
        this.client = new OkHttpClient();
        this.release = release;
        this.token = token;
    }

    public Downloader(Release release) {
        this(release, null);
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Release getRelease() {
        return release;
    }

    public String getToken() {
        return token;
    }

    public LinkedList<Asset> getAssets() {
        return release.getAssets();
    }

    public void download(File folder) {
        download(folder, new ArrayList<>());
    }

    public void download(File folder, List<String> targetFormat) {
        if (!folder.exists()) folder.mkdirs();

        final LinkedList<Asset> targetAssets = getAssets();

        if (!targetFormat.isEmpty()) {
            for (Asset asset : getAssets()) {
                final String[] format = asset.getName().split("\\.");

                if (!targetFormat.contains(format[format.length - 1])) continue;

                downloadAsset(asset, folder);
            }

            return;
        }

        targetAssets.forEach(asset -> downloadAsset(asset, folder));
    }

    private void downloadAsset(Asset asset, File folder) {
        final File file = new File(folder, asset.getName());
        final Request.Builder requestBuilder = new Request.Builder()
                .url(asset.getDownloadUrl())
                .get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "token " + token);
        }

        try (Response response = client.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) return;

            try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
                sink.writeAll(response.body().source());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
