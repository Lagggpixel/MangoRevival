package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class SetFactionHomeCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public SetFactionHomeCommand() {
    super("setfactionhome");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".setfactionhome"));
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".setfactionhome")) {
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
        Location location = p.getLocation();
        for (Claim claim : faction.getClaims()) {
          if (claim.isInside(location, true)) {
            faction.setHome(location);
            if (faction instanceof PlayerFaction) {
              if (!((PlayerFaction) faction).getOnlinePlayers().contains(p)) {
                p.sendMessage(this.lf.getString("FACTION_SET_HOME").replace("{player}", p.getName()).replace("{x}", location.getBlockX() + "").replace("{y}", location.getBlockY() + "").replace("{z}", location.getBlockZ() + ""));
              }
              ((PlayerFaction) faction).sendMessage(this.lf.getString("FACTION_SET_HOME").replace("{player}", p.getName()).replace("{x}", location.getBlockX() + "").replace("{y}", location.getBlockY() + "").replace("{z}", location.getBlockZ() + ""));
            } else {
              p.sendMessage(this.lf.getString("FACTION_SET_HOME").replace("{player}", p.getName()).replace("{x}", location.getBlockX() + "").replace("{y}", location.getBlockY() + "").replace("{z}", location.getBlockZ() + ""));
            }

            return;
          }
        }
        p.sendMessage(this.lf.getString("FACTION_NOT_INSIDE_CLAIM"));
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.FACTION_SET_HOME"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


