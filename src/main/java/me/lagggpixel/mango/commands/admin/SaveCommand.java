package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.utils.PlayerUtility;
import org.bukkit.entity.Player;

import java.io.IOException;


public class SaveCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public SaveCommand() {
    super("save");
  }

  public void execute(Player p, String[] args) {
    if (p.hasPermission(this.cf.getString("ROOT_NODE") + ".save")) {
      int systems = 0, players = 0;
      for (Faction faction : this.fm.getFactions()) {
        try {
          faction.save();
          if (faction instanceof PlayerFaction) {
            players++;
            continue;
          }
          systems++;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      for (Player player : PlayerUtility.getOnlinePlayers()) {
        if (player.hasPermission(this.cf.getString("ROOT_NODE") + ".save")) {
          player.sendMessage(this.lf.getString("SAVED.PLAYER").replace("{amount}", players + ""));
          player.sendMessage(this.lf.getString("SAVED.SYSTEM").replace("{amount}", systems + ""));
        }
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


