package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class RenameFactionCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public RenameFactionCommand() {
    super("renamefaction");
    Bukkit.getPluginManager().addPermission(new Permission(Mango.getInstance().getRootPermissionNode() + ".rename"));
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".rename")) {
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
        if (name.length() > Math.round(this.cf.getDouble("Faction.Max-Name-Length"))) {
          p.sendMessage(this.lf.getString("FACTION_TAG_EXCEED_LENGTH").replace("{length}", Math.round(this.cf.getDouble("Faction.Max-Name-Length")) + ""));

          return;
        }
        if (name.length() < Math.round(this.cf.getDouble("Faction.Min-Name-Length"))) {
          p.sendMessage(this.lf.getString("FACTION_TAG_MUST_EXCEED_LENGTH").replace("{length}", Math.round(this.cf.getDouble("Faction.Min-Name-Length")) + ""));

          return;
        }
        if (!StringUtils.isAlphanumeric(name)) {
          p.sendMessage(this.lf.getString("FACTION_TAG_INVALID"));
          return;
        }
        if (this.fm.getFactionByName(name) != null && this.fm.getFactionByName(name) != faction) {
          p.sendMessage(this.lf.getString("FACTION_TAG_EXISTS"));

          return;
        }
        if (faction.getName().equals(name)) {
          p.sendMessage(this.lf.getString("FACTION_ALREADY_TAG"));

          return;
        }
        Bukkit.broadcastMessage(this.lf.getString("FACTION_RENAME_TAG").replace("{player}", p.getName()).replace("{oldname}", faction.getName()).replace("{newname}", name));
        faction.setName(name);

        if (faction.getFile() != null && faction.getFile().exists()) {
          faction.getFile().delete();
        }
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.RENAME_FACTION"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


