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


public class UninviteCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public UninviteCommand() {
    super("uninvite", Arrays.asList("deinvite"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (args.length == 0) {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.UNINVITE"));

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
      OfflinePlayer playerToUninvite = Bukkit.getOfflinePlayer(name);

      if (playerFaction1.isLeader(p.getUniqueId()) || playerFaction1.getOfficers().contains(p.getUniqueId())) {
        if (playerFaction1.getInvitedPlayers().contains(playerToUninvite.getUniqueId())) {
          playerFaction1.getInvitedPlayers().remove(playerToUninvite.getUniqueId());
          playerFaction1.sendMessage(this.lf.getString("FACTION_PLAYER_UNINVITED").replace("{player}", p.getName()).replace("{invitedplayer}", playerToUninvite.getName()));
        } else {
          p.sendMessage(this.lf.getString("FACTION_PLAYER_NOT_INVITED_2"));
        }
      } else {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
      }
    } else {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));
    }
  }
}


