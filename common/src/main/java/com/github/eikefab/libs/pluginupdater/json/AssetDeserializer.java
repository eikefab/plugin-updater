package com.github.eikefab.libs.pluginupdater.json;

import com.github.eikefab.libs.pluginupdater.Asset;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AssetDeserializer implements JsonDeserializer<Asset> {

    @Override
    public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        final int id = object.get("id").getAsInt();
        final String name = object.get("name").getAsString();
        final String downloadUrl = object.get("browser_download_url").getAsString();
        final String contentType = object.get("content_type").getAsString();

        return new Asset(id, name, downloadUrl, contentType);
    }

}
