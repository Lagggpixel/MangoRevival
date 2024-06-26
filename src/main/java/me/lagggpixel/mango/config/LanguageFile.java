package me.lagggpixel.mango.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lagggpixel.mango.Mango;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LanguageFile {
  private final YamlConfiguration configuration;

  public LanguageFile() {
    Mango.getInstance().saveResource("lang.yml", false);
    File file = new File(Mango.getInstance().getDataFolder(), "lang.yml");
    this.configuration = YamlConfiguration.loadConfiguration(file);
  }

  public String getString(String path) {
    String string = this.configuration.getString(path);
    if (string == null) {
      return "ERROR: STRING NOT FOUND";
    }

    if (Mango.getInstance().isPlaceholderEnabled()) {
      string = PlaceholderAPI.setPlaceholders(null, string);
    }
    return ChatColor.translateAlternateColorCodes('&', string);
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
}


