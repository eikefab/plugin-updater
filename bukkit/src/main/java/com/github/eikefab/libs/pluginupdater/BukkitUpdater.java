package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.downloader.Downloader;
import com.github.eikefab.libs.pluginupdater.configuration.ConfigUpdater;
import com.google.common.io.Files;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class BukkitUpdater extends Updater {

    private final Plugin plugin;
    private final File folder;

    public BukkitUpdater(Plugin plugin, String repository, String token) {
        super(repository, token);

        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "Updates");
    }

    public BukkitUpdater(Plugin plugin, String repository) {
        this(plugin, repository, null);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable(getPlugin().getDescription().getVersion());
    }

    public BukkitUpdater download() {
        final Release release = getReleases().get(0);

        final Downloader downloader = new Downloader(release, getToken());
        final File target = new File(folder, release.getVersion());

        target.mkdirs();
        downloader.download(target, Arrays.asList("jar", "yml"));

        return this;
    }

    public void update() {
        final Release release = getReleases().get(0);
        final File target = new File(folder, release.getVersion());

        final File[] files = Objects.requireNonNull(target.listFiles());
        final ConfigUpdater configUpdater = new ConfigUpdater(plugin);

        for (File file : files) {
            if (file.getName().endsWith(".jar")) updateJar(file);
            else configUpdater.update(file);
        }
    }

    private void updateJar(File jarFile) {
        final File runningJarFile = new File(
                plugin.getDataFolder().getParent(),
                plugin.getDescription().getName() + ".jar"
        );

        if (!runningJarFile.exists()) throw new IllegalArgumentException("Original jar file not found.");

        try {
            Files.copy(jarFile, runningJarFile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
