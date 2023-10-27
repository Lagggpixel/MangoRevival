package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ClaimFactionCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final ClaimManager cm = Mango.getInstance().getClaimManager();

  public ClaimFactionCommand() {
    super("claimfaction");
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(this.cf.getString("ROOT_NODE") + ".claimfaction")) {
      if (args.length >= 1) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
          sb.append(arg).append(" ");
        }
        String name = sb.toString().trim().replace(" ", "");
        Faction faction = this.fm.getFactionByName(name);
        if (faction == null) {
          p.sendMessage(this.lf.getString("FACTION_NOT_FOUND_NAME").replace("{name}", name));

          return;
        }
        p.sendMessage(this.lf.getString("FACTION_CLAIM_OTHER").replace("{faction}", faction.getName()));
        p.getInventory().remove(this.cm.getWand());
        p.getInventory().addItem(new ItemStack[]{this.cm.getWand()});

        Mango.getInstance().getClaiming().put(p.getUniqueId(), faction);
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.CLAIM_FACTION"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


