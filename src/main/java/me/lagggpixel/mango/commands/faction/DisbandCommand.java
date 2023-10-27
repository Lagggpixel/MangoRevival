package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class DisbandCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();


  public DisbandCommand() {
    super("disband");
  }


  public void execute(Player p, String[] args) {
    if (args.length == 0) {
      if (this.fm.getFaction(p) == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
      } else if (this.fm.getFaction(p) instanceof PlayerFaction) {
        PlayerFaction faction = this.fm.getFaction(p);
        if (!faction.isLeader(p.getUniqueId()) && !p.hasPermission(this.cf.getString("ROOT_NODE") + ".disband")) {
          p.sendMessage(this.lf.getString("FACTION_MUST_BE_LEADER"));
        } else {
          Bukkit.broadcastMessage(this.lf.getString("FACTION_DISBANDED").replace("{player}", p.getName()).replace("{faction}", faction.getName()));
          faction.delete();
        }
      }

      return;
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_MANY_ARGS.DISBAND"));
  }
}


