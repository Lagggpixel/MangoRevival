package me.lagggpixel.mango.commands.admin;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;


public class FreezeCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final ClaimManager cm = Mango.getInstance().getClaimManager();

  public FreezeCommand() {
    super("freeze");
  }


  public void execute(Player p, String[] args) {
    if (p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".freeze")) {
      if (args.length >= 2) {
        Faction faction = this.fm.getFactionByName(args[0]);
        if (faction == null) {
          p.sendMessage(this.lf.getString("FACTION_NOT_FOUND_NAME").replace("{name}", args[0]));

          return;
        }
        if (!(faction instanceof PlayerFaction playerFaction)) {
          p.sendMessage(this.lf.getString("FACTION_IS_SYSTEM"));
          return;
        }
        playerFaction.freeze(getTime(args[1]));
        p.sendMessage(this.lf.getString("FACTION_FROZEN").replace("{faction}", faction.getName()).replace("{time}", playerFaction.getFreezeTime()));
      } else {

        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.FREEZE"));
      }
    } else {
      p.sendMessage(this.lf.getString("NO_PERMISSION"));
    }
  }

  private int getTime(String string) {
    int time = 0;
    if (string.contains("m")) {
      String timeStr = strip(string);
      if (NumberUtils.isNumber(timeStr)) {
        time = NumberUtils.toInt(timeStr) * 60;
      }
    } else if (string.contains("h")) {
      String timeStr = strip(string);
      if (NumberUtils.isNumber(timeStr)) {
        time = NumberUtils.toInt(timeStr) * 3600;
      }
    } else if (string.contains("s")) {
      String timeStr = strip(string);
      if (NumberUtils.isNumber(timeStr)) {
        time = NumberUtils.toInt(timeStr);
      }
    } else if (string.contains("d")) {
      String timeStr = strip(string);
      if (NumberUtils.isNumber(timeStr)) {
        time = NumberUtils.toInt(timeStr) * 86400;
      }
    } else if (string.contains("y")) {
      String timeStr = strip(string);
      if (NumberUtils.isNumber(timeStr)) {
        time = NumberUtils.toInt(timeStr) * 31536000;
      }
    }
    return time;
  }

  private String strip(String src) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < src.length(); i++) {
      char c = src.charAt(i);
      if (Character.isDigit(c)) {
        builder.append(c);
      }
    }
    return builder.toString();
  }
}


