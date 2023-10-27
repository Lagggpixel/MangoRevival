package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class VersionCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public VersionCommand() {
    super("version", Arrays.asList("ver"));
  }


  public void execute(Player p, String[] args) {
    sendMessage(p, "&6&m----------------------------------");
    sendMessage(p, "&6This server is running &eMango Version " + Mango.getInstance().getDescription().getVersion() + "&6.");
    sendMessage(p, "&6This plugin was created by &eAlexandeh &6(&ehttps://github.com/Alexandeh/&6).");
    sendMessage(p, "&6For more information regarding &eMango&6, visit &ehttp://mango.spookee.me&6.");
    sendMessage(p, "&6&m----------------------------------");
  }

  private void sendMessage(Player p, String msg) {
    p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }
}


