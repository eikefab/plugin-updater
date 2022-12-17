package com.github.eikefab.libs.pluginupdater.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUpdater {

    private final Plugin plugin;

    public ConfigUpdater(Plugin plugin) {
        this.plugin = plugin;
    }

    public void update(File file) {
        final String versionPath = "file-version";
        final String name = file.getName();

        final File original = new File(plugin.getDataFolder(), name);

        final FileConfiguration downloaded = YamlConfiguration.loadConfiguration(file);

        if (!original.exists()) {
            try {
                downloaded.save(new File(plugin.getDataFolder(), name));
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return;
        }

        final FileConfiguration config = YamlConfiguration.loadConfiguration(original);

        final int currentVersion = config.getInt(versionPath);
        final int downloadedVersion = downloaded.getInt(versionPath);

        if (currentVersion >= downloadedVersion) return;

        config.set(versionPath, downloadedVersion);

        for (String key : downloaded.getKeys(true)) {
            if (config.contains(key)) continue;

            config.set(key, downloaded.get(key));
        }

        try {
            config.save(config.saveToString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
