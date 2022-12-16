package com.github.eikefab.libs.pluginupdater.api.tests;

import com.github.eikefab.libs.pluginupdater.api.Release;
import com.github.eikefab.libs.pluginupdater.api.Updater;
import com.github.eikefab.libs.pluginupdater.api.downloader.Downloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

public class ReleaseDownloadTest {

    @Test
    public void downloadLatestRelease() {
        final Updater updater = new Updater("eikefab/folder-reader");
        updater.updateReleases();

        Assertions.assertTrue(updater.isUpdateAvailable(10));

        final Release release = updater.getReleases().get(0);

        final Downloader downloader = new Downloader(release);
        final File folder = new File(System.getProperty("user.dir"), "out");

        downloader.download(folder);

        final File file = Objects.requireNonNull(folder.listFiles())[0];
        Assertions.assertEquals(file.getName(), "folder-reader-1.1.jar");
    }

}
