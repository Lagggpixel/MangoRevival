package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;

import java.math.BigDecimal;


public class SetDTRCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final ClaimManager cm = Mango.getInstance().getClaimManager();

  public SetDTRCommand() {
    super("setdtr");
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".setdtr")) {
      if (args.length >= 2) {
        Faction faction = this.fm.getFactionByName(args[0]);
        if (faction == null) {
          p.sendMessage(this.lf.getString("FACTION_NOT_FOUND_NAME").replace("{name}", args[0]));

          return;
        }
        if (!(faction instanceof PlayerFaction)) {
          p.sendMessage(this.lf.getString("FACTION_IS_SYSTEM"));

          return;
        }
        PlayerFaction playerFaction = (PlayerFaction) faction;

        try {
          double newDTR = Double.parseDouble(args[1]);
          if (newDTR > playerFaction.getMaxDtr()) {
            newDTR = playerFaction.getMaxDtr();
          }
          playerFaction.setDtr(BigDecimal.valueOf(newDTR));
          playerFaction.sendMessage(this.lf.getString("FACTION_DTR_SET").replace("{player}", p.getName()).replace("{DTR}", newDTR + ""));
          p.sendMessage(this.lf.getString("FACTION_DTR_SET_PLAYER").replace("{faction}", faction.getName()).replace("{DTR}", newDTR + ""));
        } catch (Exception e) {
          p.sendMessage(this.lf.getString("FACTION_INVALID_DTR"));
        }
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.SETDTR"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


