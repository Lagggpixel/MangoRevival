package me.lagggpixel.mango.config;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ConfigFile {
  @Getter
  private final YamlConfiguration configuration;

  @Getter
  private static ConfigFile instance;

  public ConfigFile() {
    instance = this;
    Mango.getInstance().saveDefaultConfig();
    File file = new File(Mango.getInstance().getDataFolder(), "config.yml");
    this.configuration = YamlConfiguration.loadConfiguration(file);
  }

  public double getDouble(String path) {
    if (this.configuration.contains(path)) {
      return this.configuration.getDouble(path);
    }
    return 0.0D;
  }

  public int getInt(String path) {
    if (this.configuration.contains(path)) {
      return this.configuration.getInt(path);
    }
    return 0;
  }

  public int getInt(String path, int def) {
    if (this.configuration.contains(path)) {
      return this.configuration.getInt(path, def);
    }
    return def;
  }

  public boolean getBoolean(String path) {
    if (this.configuration.contains(path)) {
      return this.configuration.getBoolean(path);
    }
    return false;
  }

  public String getString(String path) {
    if (this.configuration.contains(path)) {
      return ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(this.configuration.getString(path)));
    }
    return "ERROR: STRING NOT FOUND";
  }

  public boolean contains(String path) {
    return this.configuration.contains(path);
  }

  public List<String> getStringList(String path) {
    if (this.configuration.contains(path)) {
      ArrayList<String> strings = new ArrayList<>();
      for (String string : this.configuration.getStringList(path)) {
        strings.add(ChatColor.translateAlternateColorCodes('&', string));
      }
      return strings;
    }
    return Collections.singletonList("ERROR: STRING LIST NOT FOUND!");
  }

  public String getBasePermission() {
    return this.getString("Permissions.Root-Permission-Node");
  }

  public String getPermission(String node) {
    return this.getString("Permissions."+ node).replace("{root}", getBasePermission());
  }
}


