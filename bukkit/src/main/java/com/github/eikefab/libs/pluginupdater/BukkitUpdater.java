package com.github.eikefab.libs.pluginupdater;

import com.google.common.io.Files;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
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

    /**
     * Check if there's an update available and alert a message
     *
     * @param alert true if you want to print the message on console
     * @param message the message, using a Function
     */
    public void checkUpdate(boolean alert, Function<Release, String[]> message) {
        query();
        if (!canUpdate()) return;

        if (alert) {
            final String[] lines = message.apply(getLatestRelease());

            for (String line : lines) plugin.getLogger().info(line);
        }
    }

    /**
     * @return true if there's a latest version that isn't a pre-release
     */
    public boolean canUpdate() {
        return canUpdate(translateVersion(plugin.getDescription().getVersion()));
    }

    /**
     * Perform update on plugin's jar file and its configuration files that are into the release assets on GitHub
     *
     * @throws IOException if occurs any I/O error
     */
    public void update() throws IOException {
        if (!canUpdate()) return;

        final Release latest = getLatestRelease();
        final File folder = new File(updatesFolder, latest.getVersion());
        final ConfigUpdater config = new ConfigUpdater(plugin);

        // Delete all old stuff
        purgeOldVersions();

        // Avoid downloading if there's already files on it
        if (folder.exists() && folder.listFiles().length >= 1) return;

        final String pluginFileName = plugin.getDescription().getName() + ".jar";
        final File pluginDataFolder = plugin.getDataFolder();
        final File pluginJarFile = new File(pluginDataFolder.getParent(), pluginFileName);

        // Don't let download if the plugin file name is not in the PluginName.jar format
        if (!pluginJarFile.exists()) throw new IllegalArgumentException("Plugin file name incompatible with the lib.");

        // Create the folder and its parent
        folder.mkdirs();

        // Download all assets
        latest.download(folder);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            final String fileName = file.getName();

            // Check if the asset file is the plugin file
            if (fileName.equals(pluginFileName)) {
                Files.copy(file, pluginJarFile);
            } else if (fileName.endsWith(".yml")) {
                // Update the .yml files within the plugin
                config.update(file);
            } else {
                // Just move the stuff the lib doesn't handle to plugin's data folder
                final File target = new File(pluginDataFolder, fileName);

                if (!target.exists()) target.createNewFile();

                Files.copy(file, target);
            }
        }
    }

    /**
     * Delete all old folders within its content
     */
    private void purgeOldVersions() {
        if (!updatesFolder.exists()) return;

        // the purgeOldVersions is called only after an canUpdate, so don't care about checks here
        final Release latest = getLatestRelease();

        final File[] files = Objects.requireNonNull(updatesFolder.listFiles());

        for (File file : files) {
            if (!file.isDirectory()) return;
            if (file.getName().equalsIgnoreCase(latest.getVersion())) return;

            file.delete();
        }
    }

}
