package com.github.eikefab.libs.pluginupdater.tests;

import com.github.eikefab.libs.pluginupdater.Release;
import com.github.eikefab.libs.pluginupdater.Updater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

public class ReleaseDownloadTest {

    @Test
    public void downloadLatestRelease() {
        final Updater updater = new Updater("eikefab/command-lib");
        updater.query();

        final Release release = updater.getLatestRelease();

        final File folder = new File(System.getProperty("user.dir"), "out");
        folder.mkdirs();

        release.download(folder);

        Assertions.assertNotEquals(0, folder.listFiles().length);
    }

}
