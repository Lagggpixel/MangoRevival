package me.lagggpixel.mango.config;

import me.lagggpixel.mango.Mango;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LanguageFile {
  private final Mango main = Mango.getInstance();
  private final File file;
  private final YamlConfiguration configuration;

  public LanguageFile() {
    this.main.saveResource("lang.yml", false);
    this.file = new File(this.main.getDataFolder(), "lang.yml");
    this.configuration = YamlConfiguration.loadConfiguration(this.file);
  }

  public String getString(String path) {
    if (this.configuration.contains(path)) {
      return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
    }
    return "ERROR: STRING NOT FOUND";
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


