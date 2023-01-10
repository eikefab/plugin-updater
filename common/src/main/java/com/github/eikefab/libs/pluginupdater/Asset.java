package com.github.eikefab.libs.pluginupdater;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;

/**
 * Representation of a release's asset on GitHub
 */
public class Asset {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

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

    public void download(String token, File folder) {
        final File file = new File(folder, name);
        final Request.Builder requestBuilder = new Request.Builder()
                .url(downloadUrl)
                .get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "token " + token);
        }

        try (Response response = HTTP_CLIENT.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) return;

            final ResponseBody body = response.body();
            if (body == null) return;

            try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
                sink.writeAll(body.source());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(File folder) {
        download(null, folder);
    }

}
