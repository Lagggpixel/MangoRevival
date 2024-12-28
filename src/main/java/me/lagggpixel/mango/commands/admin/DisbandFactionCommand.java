package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class DisbandFactionCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public DisbandFactionCommand() {
    super("disbandfaction");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".disbandfaction"));
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".disbandfaction")) {
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
        Bukkit.broadcastMessage(this.lf.getString("FACTION_DISBANDED").replace("{player}", p.getName()).replace("{faction}", faction.getName()));
        this.fm.getFactions().remove(faction);
        faction.delete();
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.DISBAND_FACTION"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


