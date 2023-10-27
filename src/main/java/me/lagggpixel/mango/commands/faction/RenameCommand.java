package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class RenameCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public RenameCommand() {
    super("rename", Arrays.asList("tag"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (args.length == 0) {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.RENAME"));

      return;
    }
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
    if (this.fm.getFactionByName(name) != null && this.fm.getFactionByName(name) != playerFaction) {
      p.sendMessage(this.lf.getString("FACTION_TAG_EXISTS"));

      return;
    }
    if (playerFaction.getName().equals(name)) {
      p.sendMessage(this.lf.getString("FACTION_ALREADY_TAG"));

      return;
    }
    Bukkit.broadcastMessage(this.lf.getString("FACTION_RENAME_TAG").replace("{player}", p.getName()).replace("{oldname}", playerFaction.getName()).replace("{newname}", name));
    playerFaction.setName(name);
    if (playerFaction.getFile() != null && playerFaction.getFile().exists()) playerFaction.getFile().delete();
  }
}


