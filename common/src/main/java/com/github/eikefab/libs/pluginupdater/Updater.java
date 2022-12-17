package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.deserializer.AssetDeserializer;
import com.github.eikefab.libs.pluginupdater.deserializer.ReleaseDeserializer;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Updater {

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

    /**
     * Must be used after query, otherwise it always will return an empty LinkedList
     *
     * @return the releases
     */
    public LinkedList<Release> getReleases() {
        return releases;
    }

    /**
     * Retrieves the repository and update the list of releases
     *
     * @return all releases found on GitHub, the latest one will always be the 1st on the list
     */
    public LinkedList<Release> query() {
        releases.clear();

        final Request.Builder requestBuilder = new Request.Builder()
                .url(String.format("https://api.github.com/repos/%s/releases", repository))
                .get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "token " + token);
        }

        final Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return releases;

            final List<JsonElement> list = gson.fromJson(response.body().string(), JsonArray.class).asList();

            releases.addAll(
                    list.stream()
                        .map(element -> gson.fromJson(element, Release.class))
                        .collect(Collectors.toList())
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return releases;
    }

    /**
     * Compares the provided version to the latest release version
     *
     * @param currentVersion the version to compare
     * @return true if the latest release is newer than the current one
     */
    public boolean isUpdateAvailable(int currentVersion) {
        final List<Release> releases = getReleases();

        if (releases.isEmpty()) return false;

        final Release release = releases.get(0);

        try {
            final int version = Integer.parseInt(release.getVersion().replace(".", ""));

            return version > currentVersion;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Release 'tag_name' isn't supported!");
        }
    }

    public boolean isUpdateAvailable(String currentVersion) {
        try {
            final int version = Integer.parseInt(currentVersion.replace(".", ""));

            return isUpdateAvailable(version);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Current 'tag_name' isn't supported!");
        }
    }

}
