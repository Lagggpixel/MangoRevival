package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;


public class UnclaimCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public UnclaimCommand() {
    super("unclaim");
  }


  public void execute(Player p, String[] args) {
    for (Claim claim : Mango.getInstance().getClaimManager().getClaims()) {
      if (claim.isInside(p.getLocation(), true)) {
        Faction faction = claim.getOwner();
        if (faction != this.fm.getFaction(p) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
          p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
          return;
        }
        if (faction instanceof PlayerFaction) {
          PlayerFaction playerFaction = (PlayerFaction) faction;
          if (!playerFaction.getOfficers().contains(p.getUniqueId()) && !playerFaction.isLeader(p.getUniqueId()) && !p.hasPermission(this.cf.getString("ROOT_NODE") + ".unclaim")) {
            p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));

            return;
          }
        }
        if (faction.getHome() != null && claim.isInside(faction.getHome(), false)) {
          faction.setHome(null);
        }


        faction.getClaims().remove(claim);
        Mango.getInstance().getClaimManager().getClaims().remove(claim);
        if (faction instanceof PlayerFaction) {
          ((PlayerFaction) faction).sendMessage(this.lf.getString("FACTION_LAND_UNCLAIMED").replace("{player}", p.getName()));
          ((PlayerFaction) faction).setBalance(((PlayerFaction) faction).getBalance() + claim.getValue());
          if (!((PlayerFaction) faction).getOnlinePlayers().contains(p)) {
            p.sendMessage(this.lf.getString("FACTION_LAND_UNCLAIMED").replace("{player}", p.getName()));
          }
        } else {
          p.sendMessage(this.lf.getString("FACTION_LAND_UNCLAIMED").replace("{player}", p.getName()));
        }
        return;
      }
    }
    p.sendMessage(this.lf.getString("FACTION_NOT_INSIDE_CLAIM"));
  }
}


