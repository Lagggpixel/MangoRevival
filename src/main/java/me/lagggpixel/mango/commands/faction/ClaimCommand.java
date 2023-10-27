package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ClaimCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ClaimCommand() {
    super("claim");
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (playerFaction instanceof PlayerFaction) {
      PlayerFaction playerFaction1 = playerFaction;

      if (playerFaction1.getMembers().contains(p.getUniqueId())) {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));

        return;
      }
      p.getInventory().remove(Mango.getInstance().getClaimManager().getWand());
      p.getInventory().addItem(new ItemStack[]{Mango.getInstance().getClaimManager().getWand()});
    }
  }
}


