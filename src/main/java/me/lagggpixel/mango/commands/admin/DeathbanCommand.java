package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class DeathbanCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public DeathbanCommand() {
    super("deathban");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".deathban"));
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".deathban")) {
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
        if (name.equalsIgnoreCase("true")) {
          systemFaction.setDeathban(true);
        } else if (name.equalsIgnoreCase("false")) {
          systemFaction.setDeathban(false);
        } else {
          p.sendMessage(this.lf.getString("FACTION_INVALID_ARGS").replace("{cmd}", name));
          return;
        }
        p.sendMessage(this.lf.getString("SYSTEM_SET_DEATHBAN").replace("{deathban}", systemFaction.isDeathban()).replace("{faction}", faction.getName()));
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.DEATHBAN"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


