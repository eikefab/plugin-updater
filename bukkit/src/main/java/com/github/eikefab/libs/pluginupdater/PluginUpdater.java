package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.api.Release;
import com.github.eikefab.libs.pluginupdater.api.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PluginUpdater extends JavaPlugin {

    private final BukkitUpdater updater;

    public PluginUpdater() {
        this.updater = new BukkitUpdater(this,"eikefab/plugin-updater");
    }

    @Override
    public void onEnable() {
        getUpdater().query();
        getDataFolder().mkdir();
        checkUpdates();
    }

    @Override
    public void onDisable() {
        if (!updater.isUpdateAvailable()) return;

        updater.download().update();
    }

    public Updater getUpdater() {
        return updater;
    }

    private void checkUpdates() {
        if (!updater.isUpdateAvailable()) return;

        final Logger logger = getLogger();
        final Release release = updater.getReleases().get(0);

        if (release.isPreRelease()) return;

        String[] messages = new String[] {
                "#".repeat(30),
                "There's a new update!",
                "Your version: " + getDescription().getVersion(),
                "Available version: " + release.getVersion(),
                "Update:",
                " - " + release.getBody(),
                "Available at: " + release.getUrl(),
                "#".repeat(30)
        };

        for (String message : messages) {
            logger.info(message);
        }
    }

    public static PluginUpdater getInstance() {
        return getPlugin(PluginUpdater.class);
    }

}
