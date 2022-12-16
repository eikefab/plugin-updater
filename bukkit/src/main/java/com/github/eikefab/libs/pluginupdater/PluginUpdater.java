package com.github.eikefab.libs.pluginupdater;

import com.github.eikefab.libs.pluginupdater.api.Release;
import com.github.eikefab.libs.pluginupdater.api.Updater;
import com.github.eikefab.libs.pluginupdater.api.downloader.Downloader;
import com.google.common.io.Files;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public final class PluginUpdater extends JavaPlugin {

    private final File folder;
    private final File pluginsFolder;
    private final PluginDescriptionFile description;

    private final Updater updater;

    public PluginUpdater() {
        this.folder = getDataFolder();
        this.pluginsFolder = folder.getParentFile();
        this.description = getDescription();
        this.updater = new Updater("eikefab/plugin-updater");
        this.updater.updateReleases();
    }

    @Override
    public void onEnable() {
        folder.mkdirs();
        checkUpdates();
    }

    private void checkUpdates() {
        final String version = description.getVersion();

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
        final File updateFolder = new File(folder, String.format("%s/%s", plugin.getName(), release.getVersion()));

        if (!updateFolder.mkdirs()) return;

        downloader.download(updateFolder, Arrays.asList("jar", "yml"));

        final File[] files = Objects.requireNonNull(updateFolder.listFiles());

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                final String fullName = description + "-" + description.getVersion() + ".jar";
                final File originalFile = new File(pluginsFolder, fullName);

                if (!originalFile.exists()) throw new IllegalArgumentException("Plugin filename isn't supported.");
                originalFile.deleteOnExit();

                final File releasePluginFile = new File(pluginsFolder, file.getName());

                try {
                    releasePluginFile.createNewFile();
                    Files.copy(file, releasePluginFile);
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
