package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class ThawCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ThawCommand() {
    super("thaw");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".thaw"));
  }

  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".thaw")) {
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
        if (!(faction instanceof PlayerFaction)) {
          p.sendMessage(this.lf.getString("FACTION_IS_SYSTEM"));

          return;
        }
        PlayerFaction playerFaction = (PlayerFaction) faction;

        if (!playerFaction.isFrozen()) {
          p.sendMessage(this.lf.getString("FACTION_NOT_FROZEN"));

          return;
        }
        playerFaction.unfreeze();
        playerFaction.sendMessage(this.lf.getString("FACTION_THAWED").replace("{player}", p.getName()));
        p.sendMessage(this.lf.getString("FACTION_THAWED_PLAYER").replace("{faction}", faction.getName()));
      } else {
        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.THAW"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


