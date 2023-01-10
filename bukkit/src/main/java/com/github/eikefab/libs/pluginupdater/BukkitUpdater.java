package com.github.eikefab.libs.pluginupdater;

import com.google.common.io.Files;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

public class BukkitUpdater extends Updater {

    private final Plugin plugin;
    private final File updatesFolder;

    public BukkitUpdater(Plugin plugin, String repository, String token) {
        super(repository, token);

        this.plugin = plugin;
        this.updatesFolder = new File(plugin.getDataFolder(), "Updates");
    }

    public BukkitUpdater(Plugin plugin, String repository) {
        this(plugin, repository, null);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void checkUpdate(boolean alert, Function<Release, String[]> message) {
        query();
        if (!canUpdate()) return;

        if (alert) {
            final String[] lines = message.apply(getLatestRelease());

            for (String line : lines) plugin.getLogger().info(line);
        }
    }

    public boolean canUpdate() {
        return canUpdate(translateVersion(plugin.getDescription().getVersion()));
    }

    public void update() {
        if (!canUpdate()) return;

        final Release latest = getLatestRelease();
        final File folder = new File(updatesFolder, latest.getVersion());
        final ConfigUpdater config = new ConfigUpdater(plugin);

        purgeOldVersions();
        if (folder.exists()) return;

        final String pluginFileName = plugin.getDescription().getName();
        final File pluginJarFile = new File(plugin.getDataFolder().getParent(), pluginFileName);

        if (!pluginJarFile.exists()) throw new IllegalArgumentException("Plugin file name incompatible with the lib.");

        folder.mkdirs();
        latest.download(folder);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            final String fileName = file.getName();

            if (fileName.equals(pluginFileName)) {
                try {
                    Files.copy(file, pluginJarFile);
                } catch (Exception exception) {
                    exception.printStackTrace();

                    break;
                }
            } else if (fileName.endsWith(".yml")) {
                config.update(file);
            }
        }
    }

    private void purgeOldVersions() {
        if (!updatesFolder.exists()) return;

        final Release latest = getLatestRelease();
        final File[] files = Objects.requireNonNull(updatesFolder.listFiles());

        for (File file : files) {
            if (!file.isDirectory()) return;
            if (file.getName().equalsIgnoreCase(latest.getVersion())) return;

            file.delete();
        }
    }

}
