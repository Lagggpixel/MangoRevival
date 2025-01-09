package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class ColorCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ColorCommand() {
    super("color");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".color"));
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".color")) {
      if (args.length >= 2) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
          sb.append(args[i]).append(" ");
        }
        String name = sb.toString().trim().replace(" ", "");
        Faction faction = this.fm.getFactionByName(args[0]);
        if (faction == null) {
          p.sendMessage(this.lf.getString("FACTION_NOT_FOUND_NAME").replace("{name}", name));

          return;
        }
        if (!(faction instanceof SystemFaction)) {
          p.sendMessage(this.lf.getString("FACTION_NOT_SYSTEM"));

          return;
        }
        SystemFaction systemFaction = (SystemFaction) faction;

        try {
          ChatColor color = ChatColor.valueOf(name.toUpperCase());
          systemFaction.setColor(color);
          p.sendMessage(this.lf.getString("FACTION_SET_COLOR").replace("{faction}", faction.getName()).replace("{color}", color + color.name()));
        } catch (IllegalArgumentException e) {
          p.sendMessage(this.lf.getString("FACTION_INVALID_COLOR"));
        }
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.COLOR"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


