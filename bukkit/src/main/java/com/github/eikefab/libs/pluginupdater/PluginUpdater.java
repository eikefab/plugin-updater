package com.github.eikefab.libs.pluginupdater;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PluginUpdater extends JavaPlugin {

    private final BukkitUpdater updater;

    public PluginUpdater() {
        this.updater = new BukkitUpdater(this,"eikefab/plugin-updater");
    }

    @Override
    public void onEnable() {
        updater.checkUpdate(
                true,
                (release) -> new String[] {
                    "There's a new update!",
                    "Your version: " + getDescription().getVersion(),
                    "Available version: " + release.getVersion(),
                    "Update:",
                    " - " + release.getBody(),
                    "Available at: " + release.getUrl(),
                }
        );
    }

    @Override
    public void onDisable() {
        try {
            updater.update();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Updater getUpdater() {
        return updater;
    }

    public static PluginUpdater getInstance() {
        return getPlugin(PluginUpdater.class);
    }

}
