package com.github.eikefab.libs.pluginupdater.tests;


import com.github.eikefab.libs.pluginupdater.Updater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateAvailableTest {

    @Test
    public void testIfReleasesAreBeingRequestedCorrectly() {
        final Updater updater = new Updater("eikefab/command-lib");

        updater.query();

        Assertions.assertTrue(updater.canUpdate(99));
    }

}
