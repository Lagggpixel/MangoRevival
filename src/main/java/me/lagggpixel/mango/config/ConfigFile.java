package me.lagggpixel.mango.config;

import me.lagggpixel.mango.Mango;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigFile {
  private final Mango main = Mango.getInstance();
  private final File file;
  private final YamlConfiguration configuration;

  public ConfigFile() {
    this.main.saveDefaultConfig();
    this.file = new File(this.main.getDataFolder(), "config.yml");
    this.configuration = YamlConfiguration.loadConfiguration(this.file);
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
      return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
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
    return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
  }
}


