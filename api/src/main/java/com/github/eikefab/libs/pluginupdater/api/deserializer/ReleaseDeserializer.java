package com.github.eikefab.libs.pluginupdater.api.deserializer;

import com.github.eikefab.libs.pluginupdater.api.Asset;
import com.github.eikefab.libs.pluginupdater.api.Release;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ReleaseDeserializer implements JsonDeserializer<Release> {

    @Override
    public Release deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        final int id = object.get("id").getAsInt();
        final String body = object.get("body").getAsString();
        final String version = object.get("tag_name").getAsString();
        final boolean preRelease = object.get("prerelease").getAsBoolean();
        final String url = object.get("html_url").getAsString();
        final LinkedList<Asset> assets =
                object.get("assets").getAsJsonArray()
                .asList()
                .stream()
                .map(element -> context.<Asset>deserialize(element, Asset.class))
                .collect(Collectors.toCollection(LinkedList::new));

        return new Release(id, body, version, preRelease, url, assets);
    }

}
