# plugin-updater
A simple lib that allows update-checking and auto-update.

# Import

## Gradle

```groovy
repositories {
    maven { url = "https://jitpack.io" }
}

dependencies {
    compileOnly "com.github.eikefab.plugin-updater:bukkit:master-SNAPSHOT"
}
```

# Bukkit

## Requirements
The plugin file name must be in the `PluginName.jar` format like WorldGuard.jar, Essentials.jar, etc.

It's not required, but it would be ok, is, if you would use the config update function, add the `file-version` field on
it, like

`config.yml`
```yaml
my-random-stuff:
  foo:
    bar: "hello"

file-version: 1
```

If there's no `file-version`, it would be by default `1`.

### Updating config files
The config file must be, as the plugin file, on GitHub release's assets.

## Usage

First, let's create a `BukkitUpdater` instance.
```java
final BukkitUpdater updater = new BukkitUpdater(plugin, repository, token);
```

The token param is a OAUTH2 GitHub token to allow the library to search through private repositories, but if you don't
want it, you can set it null or just remove it from the constructor.

```java
final BukkitUpdater updater = new BukkitUpdater(plugin, repository);
```

The repository param is the GitHub repository in the `Author/Repository` format, like `eikefab/plugin-updater`.
And the plugin parameter is the plugin's main instance.

Then, you would need to query the GitHub API, using

```java
updater.query();
```

But, if you want to use the included checker, it doesn't require that, just:

```java
updater.checkUpdate(true, (release) -> new String[] { "New version: " + release.getVersion() });
```

If the first param is true, then would send the message using plugin logger at INFO level.

### Apply the jar file and update .yml files
```java
updater.update();
```

It would check if the updater can apply the update by replacing the jar file and update the configuration files within
the assets.

## Full example
```java
public final class MyPlugin extends JavaPlugin {
    
    private final BukkitUpdater updater;
    
    public MyPlugin() {
        this.updater = new BukkitUpdater(this, "MyGitHubUserName/MyRepository");
    }
    
    @Override
    public void onEnable() {
        updater.checkUpdate(false, ($) -> new String[] {});
    }
    
    @Override
    public void onDisable() {
        updater.update();
    }
    
}
```

Then, in the next plugin startup, it would be the latest version running. 

<b>If you don't want to shade and relocate the lib and its dependencies (3mb~) or would use it in more than one plugin, 
put `depend: [PluginUpdater]` on your `plugin.yml` and download the library as a plugin</b>.
