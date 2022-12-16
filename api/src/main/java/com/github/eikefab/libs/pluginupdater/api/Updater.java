package com.github.eikefab.libs.pluginupdater.api;

import com.github.eikefab.libs.pluginupdater.api.deserializer.ReleaseDeserializer;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class Updater {

    private final Gson gson;
    private final OkHttpClient client;
    private final String repository;

    public Updater(String repository) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Release.class, new ReleaseDeserializer())
                .create();
        this.client = new OkHttpClient();
        this.repository = repository;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getRepository() {
        return repository;
    }

    /**
     * Retrieves the repository release, returns an empty LinkedList if it was not successful
     *
     * @return all releases found on GitHub, the latest one will always be the 1st on the list
     */
    public LinkedList<Release> getReleases() {
        final LinkedList<Release> releases = new LinkedList<>();
        final Request request = new Request.Builder()
                .url(String.format("https://api.github.com/repos/%s/releases", repository))
                .get()
                .build();

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
     * Compares the current version to the latest release version
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

}
