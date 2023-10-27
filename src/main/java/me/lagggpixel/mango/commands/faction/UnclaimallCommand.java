package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;


public class UnclaimallCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public UnclaimallCommand() {
    super("unclaimall");
  }


  public void execute(Player p, String[] args) {
    PlayerFaction faction = this.fm.getFaction(p);
    if (faction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
      return;
    }
    if (faction.getOfficers().contains(p.getUniqueId()) || faction.isLeader(p.getUniqueId()) || p.hasPermission(this.cf.getString("ROOT_NODE") + ".unclaimall")) {
      if (faction.getClaims().isEmpty()) {
        p.sendMessage(this.lf.getString("FACTION_NO_CLAIMS"));
        return;
      }
      faction.sendMessage(this.lf.getString("FACTION_UNClAIM_ALL").replace("{player}", p.getName()));
      for (Claim claim : faction.getClaims()) {
        Mango.getInstance().getClaimManager().getClaims().remove(claim);
      }
      faction.getClaims().clear();
    } else {
      p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
    }
  }
}


