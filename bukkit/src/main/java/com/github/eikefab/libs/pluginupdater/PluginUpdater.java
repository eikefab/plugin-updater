package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.api.Release;
import com.github.eikefab.libs.pluginupdater.api.Updater;
import com.github.eikefab.libs.pluginupdater.api.downloader.Downloader;
import com.google.common.io.Files;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public final class PluginUpdater extends JavaPlugin {

    private final File folder;
    private final File pluginsFolder;
    private final Updater updater;

    public PluginUpdater() {
        this.folder = getDataFolder();
        this.pluginsFolder = folder.getParentFile();
        this.updater = new Updater("eikefab/plugin-updater");
        this.updater.updateReleases();
    }

    @Override
    public void onEnable() {
        folder.mkdirs();
        checkUpdates();
    }

    private void checkUpdates() {
        final String version = getDescription().getVersion();

        if (!updater.isUpdateAvailable(version)) return;

        final Logger logger = getLogger();
        final Release release = updater.updateReleases().get(0);

        if (release.isPreRelease()) return;

        String[] messages = new String[] {
                "#".repeat(30),
                "There's a new update!",
                "Your version: " + version,
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

    public void updateFiles(Plugin plugin, Release release, String token) {
        final Downloader downloader = new Downloader(release, token);
        final File updateFolder = new File(folder, String.format("%s_%s", plugin.getName(), release.getVersion()));

        if (!updateFolder.exists()) updateFolder.mkdir();

        downloader.download(updateFolder, Arrays.asList("jar", "yml"));

        final File[] files = Objects.requireNonNull(updateFolder.listFiles());

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                final PluginDescriptionFile description = plugin.getDescription();
                final String fullName = description.getName() + "-" + description.getVersion() + ".jar";

                final File originalFile = new File(pluginsFolder, fullName);

                if (!originalFile.exists()) throw new IllegalArgumentException("Plugin filename isn't supported.");

                try {
                    Files.copy(file, originalFile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                // TODO: implement configuration files overwrite
            }
        }
    }

    public static PluginUpdater getInstance() {
        return getPlugin(PluginUpdater.class);
    }

}
