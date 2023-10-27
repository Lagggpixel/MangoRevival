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


public class LeaderCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public LeaderCommand() {
    super("leader", Arrays.asList(new String[]{"owner"}));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (args.length == 0) {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.LEADER"));

      return;
    }
    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (playerFaction instanceof PlayerFaction) {
      PlayerFaction playerFaction1 = playerFaction;

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < args.length; i++) {
        sb.append(args[i]).append(" ");
      }

      String name = sb.toString().trim().replace(" ", "");
      OfflinePlayer newLeader = Bukkit.getOfflinePlayer(name);

      if (!playerFaction1.getPlayers().contains(newLeader)) {
        p.sendMessage(this.lf.getString("FACTION_PLAYER_NOT_FOUND").replace("{player}", name));

        return;
      }
      if (playerFaction1.isLeader(p.getUniqueId())) {
        if (p.getName().equalsIgnoreCase(name)) {
          p.sendMessage(this.lf.getString("FACTION_ALREADY_LEADER"));

          return;
        }
        playerFaction1.setLeader(newLeader.getUniqueId());
        playerFaction1.getOfficers().remove(newLeader.getUniqueId());
        playerFaction1.getMembers().remove(newLeader.getUniqueId());
        playerFaction1.getOfficers().add(p.getUniqueId());
        playerFaction1.sendMessage(this.lf.getString("FACTION_GIVE_LEADER").replace("{player}", p.getName()).replace("{leader}", name));
      } else {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_LEADER"));
      }
    } else {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));
    }
  }
}


