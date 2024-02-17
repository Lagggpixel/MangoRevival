package me.lagggpixel.mango.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lagggpixel.mango.Mango;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
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
    return List.of("ERROR: STRING LIST NOT FOUND!");
  }
}


