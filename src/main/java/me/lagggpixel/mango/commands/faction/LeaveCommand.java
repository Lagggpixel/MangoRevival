package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;


public class LeaveCommand extends FactionSubCommand {
  private final FactionManager factionManager = Mango.getInstance().getFactionManager();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();


  public LeaveCommand() {
    super("leave");
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction1 = this.factionManager.getFaction(p);

    if (playerFaction1 == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (!(playerFaction1 instanceof PlayerFaction)) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));

      return;
    }
    PlayerFaction playerFaction = playerFaction1;

    if (playerFaction.isLeader(p.getUniqueId())) {
      p.sendMessage(this.lf.getString("FACTION_MUST_GIVE_LEADER_ROLE"));

      return;
    }
    if (playerFaction.isFrozen() && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
      p.sendMessage(this.lf.getString("FACTION_LEAVE_FROZEN"));

      return;
    }
    if (playerFaction.getOfficers().contains(p.getUniqueId())) {
      playerFaction.getOfficers().remove(p.getUniqueId());
    } else {

      playerFaction.getMembers().remove(p.getUniqueId());
    }
    playerFaction.sendMessage(this.lf.getString("FACTION_PLAYER_LEFT").replace("{player}", p.getName()));
    p.sendMessage(this.lf.getString("FACTION_LEFT_FACTION"));
  }
}


