package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class OfficerCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public OfficerCommand() {
    super("officer", Arrays.asList("mod", "promote"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (args.length == 0) {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.OFFICER"));

      return;
    }
    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (playerFaction instanceof PlayerFaction) {
      PlayerFaction playerFaction1 = playerFaction;

      StringBuilder sb = new StringBuilder();
      for (String arg : args) {
        sb.append(arg).append(" ");
      }

      String name = sb.toString().trim().replace(" ", "");
      OfflinePlayer newOfficer = Bukkit.getOfflinePlayer(name);

      if (!playerFaction1.getPlayers().contains(newOfficer)) {
        p.sendMessage(this.lf.getString("FACTION_PLAYER_NOT_FOUND").replace("{player}", name));

        return;
      }
      if (playerFaction1.isLeader(p.getUniqueId())) {
        if (playerFaction1.getLeader() == newOfficer.getUniqueId()) {
          p.sendMessage(this.lf.getString("FACTION_CANNOT_DEMOTE"));

          return;
        }
        if (playerFaction1.getOfficers().contains(newOfficer.getUniqueId())) {
          p.sendMessage(this.lf.getString("FACTION_ALREADY_OFFICER"));

          return;
        }
        playerFaction1.getMembers().remove(newOfficer.getUniqueId());
        playerFaction1.getOfficers().add(newOfficer.getUniqueId());
        playerFaction1.sendMessage(this.lf.getString("FACTION_GIVE_OFFICER").replace("{player}", p.getName()).replace("{officer}", newOfficer.getName()));
      } else {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_LEADER"));
      }
    } else {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));
    }
  }
}


