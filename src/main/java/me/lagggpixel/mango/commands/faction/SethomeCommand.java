package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class SethomeCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public SethomeCommand() {
    super("sethome", Arrays.asList("addhome"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (playerFaction instanceof PlayerFaction) {
      PlayerFaction playerFaction1 = playerFaction;
      if (playerFaction1.isLeader(p.getUniqueId()) || playerFaction1.getOfficers().contains(p.getUniqueId())) {
        Location location = p.getLocation();
        for (Claim claim : playerFaction.getClaims()) {
          if (claim.isInside(location, true)) {
            playerFaction1.setHome(location);
            playerFaction1.sendMessage(this.lf.getString("FACTION_SET_HOME").replace("{player}", p.getName()).replace("{x}", location.getBlockX() + "").replace("{y}", location.getBlockY() + "").replace("{z}", location.getBlockZ() + ""));
            return;
          }
        }
        p.sendMessage(this.lf.getString("FACTION_NOT_INSIDE_CLAIM"));
      } else {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
      }
    } else {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));
    }
  }
}


