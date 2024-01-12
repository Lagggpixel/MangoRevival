package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class CreateCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public CreateCommand() {
    super("create");
  }


  public void execute(Player p, String[] args) {
    if (args.length >= 1) {

      if (this.fm.getFaction(p) != null) {
        p.sendMessage(this.lf.getString("FACTION_ALREADY_IN"));

        return;
      }
      StringBuilder sb = new StringBuilder();
      for (String arg : args) {
        sb.append(arg).append(" ");
      }
      String name = sb.toString().trim().replace(" ", "");

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
      for (Faction faction : this.fm.getFactions()) {
        if (faction.getName().equalsIgnoreCase(name)) {
          p.sendMessage(this.lf.getString("FACTION_TAG_EXISTS"));

          return;
        }
      }
      Bukkit.broadcastMessage(this.lf.getString("FACTION_CREATED").replace("{player}", p.getName()).replace("{faction}", name));

      PlayerFaction playerFaction = new PlayerFaction(name, p.getUniqueId());
      this.fm.getFactions().add(playerFaction);
    } else {

      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.CREATE"));
    }
  }
}


