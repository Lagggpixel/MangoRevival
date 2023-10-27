package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class CreateSystemCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public CreateSystemCommand() {
    super("createsystem");
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(this.cf.getString("ROOT_NODE") + ".createsystem")) {
      if (args.length >= 1) {

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
          sb.append(arg).append(" ");
        }
        String name = sb.toString().trim().replace(" ", "");

        if (name.length() > Math.round(this.cf.getDouble("MAX_NAME_LENGTH"))) {
          p.sendMessage(this.lf.getString("FACTION_TAG_EXCEED_LENGTH").replace("{length}", Math.round(this.cf.getDouble("MAX_NAME_LENGTH")) + ""));

          return;
        }
        if (name.length() < Math.round(this.cf.getDouble("MIN_NAME_LENGTH"))) {
          p.sendMessage(this.lf.getString("FACTION_TAG_MUST_EXCEED_LENGTH").replace("{length}", Math.round(this.cf.getDouble("MIN_NAME_LENGTH")) + ""));

          return;
        }

        if (!StringUtils.isAlphanumeric(name)) {
          p.sendMessage(this.lf.getString("FACTION_TAG_INVALID"));

          return;
        }
        for (Faction faction : this.fm.getFactions()) {
          if (faction.getName().equalsIgnoreCase(name)) {
            p.sendMessage(this.lf.getString("FACTION_TAG_EXISTS"));

            return;
          }
        }
        Bukkit.broadcastMessage(this.lf.getString("SYSTEM_CREATED").replace("{player}", p.getName()).replace("{faction}", name));

        SystemFaction systemFaction = new SystemFaction(name);
        this.fm.getFactions().add(systemFaction);
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.CREATE_SYSTEM"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }
}


