package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import org.bukkit.entity.Player;


public class HelpCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public HelpCommand() {
    super("help");
  }


  public void execute(Player p, String[] args) {
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("4")) {
        for (String msg : this.lf.getStringList("FACTION_HELP.4")) {
          p.sendMessage(msg);
        }
      } else if (args[0].equalsIgnoreCase("3")) {
        for (String msg : this.lf.getStringList("FACTION_HELP.3")) {
          p.sendMessage(msg);
        }
      } else if (args[0].equalsIgnoreCase("2")) {
        for (String msg : this.lf.getStringList("FACTION_HELP.2")) {
          p.sendMessage(msg);
        }
      } else {
        for (String msg : this.lf.getStringList("FACTION_HELP.1")) {
          p.sendMessage(msg);
        }
      }
    } else {
      for (String msg : this.lf.getStringList("FACTION_HELP.1"))
        p.sendMessage(msg);
    }
  }
}


