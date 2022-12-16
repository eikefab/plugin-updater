package com.github.eikefab.libs.pluginupdater.api.tests;


import com.github.eikefab.libs.pluginupdater.api.Updater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateAvailableTest {

    @Test
    public void testIfReleasesAreBeingRequestedCorrectly() {
        final Updater updater = new Updater("eikefab/folder-reader");

        updater.updateReleases();

        Assertions.assertTrue(updater.isUpdateAvailable(10));
    }

}
