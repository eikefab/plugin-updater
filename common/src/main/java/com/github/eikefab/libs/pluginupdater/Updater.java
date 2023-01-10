package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.json.AssetDeserializer;
import com.github.eikefab.libs.pluginupdater.json.ReleaseDeserializer;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Updater {

    private static final String REPOSITORY_URL = "https://api.github.com/repos/%s/releases";

    private final Gson gson;
    private final OkHttpClient client;
    private final LinkedList<Release> releases;
    private final String repository;
    private final String token;

    public Updater(String repository, String token) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Release.class, new ReleaseDeserializer())
                .registerTypeAdapter(Asset.class, new AssetDeserializer())
                .create();
        this.client = new OkHttpClient();
        this.releases = new LinkedList<>();
        this.repository = repository;
        this.token = token;
    }

    public Updater(String repository) {
        this(repository, null);
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getRepository() {
        return repository;
    }

    public String getToken() {
        return token;
    }

    public LinkedList<Release> getReleases() {
        return releases;
    }

    /**
     * @return the latest release or null if releases list is empty
     */
    public Release getLatestRelease() {
        if (releases.isEmpty()) return null;

        return releases.getFirst();
    }

    /**
     * Retrieve info from GitHub
     */
    public void query() {
        final Request.Builder requestBuilder = new Request.Builder()
                .url(String.format(REPOSITORY_URL, repository))
                .get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "token " + token);
        }

        final Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return;

            final ResponseBody body = response.body();
            if (body == null) return;

            final List<JsonElement> list = gson.fromJson(body.string(), JsonArray.class).asList();

            releases.addAll(
                    list.stream()
                        .map(element -> gson.fromJson(element, Release.class))
                        .collect(Collectors.toList())
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Translate a semantic version to integer
     *
     * @param version the version itself
     * @return the semantic version as a number, example: 1.0.0 as 100
     */
    public int translateVersion(String version) {
        try {
            return Integer.parseInt(version.replace(".", ""));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Check if the latest version is higher than current version
     *
     * @param version the version to compare
     * @return false if they are equal or if the latest release is a pre-release one, true if and only the latest
     * release version is higher than current one
     */
    public boolean canUpdate(int version) {
        final Release latest = getLatestRelease();

        if (latest == null) return false;
        if (latest.isPreRelease()) return false;

        final int latestVersion = translateVersion(latest.getVersion());

        if (latestVersion == -1) throw new IllegalArgumentException("Couldn't check release version.");

        return latestVersion > version;
    }

    public void download(File folder) {
        final Release latest = getLatestRelease();

        if (latest == null) return;

        latest.download(token, folder);
    }

    /**
     * Translates the version and then calls canUpdate(integer)
     * @param version the semantic version
     * @return false if they are equal or if the latest release is a pre-release one, true if and only the latest
     * release version is higher than current one
     */
    public boolean canUpdate(String version) {
        return canUpdate(translateVersion(version));
    }

}
