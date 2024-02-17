package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


public class KickCommand extends FactionSubCommand {
  private final FactionManager factionManager = Mango.getInstance().getFactionManager();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();

  public KickCommand() {
    super("kick");
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.factionManager.getFaction(p);


    if (args.length >= 1) {

      if (playerFaction == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

        return;
      }

      if (!playerFaction.isLeader(p.getUniqueId()) && !playerFaction.getOfficers().contains(p.getUniqueId())) {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_LEADER_OR_OFFICER"));

        return;
      }
      StringBuilder sb = new StringBuilder();
      for (String arg : args) {
        sb.append(arg).append(" ");
      }
      String name = sb.toString().trim().replace(" ", "");

      if (playerFaction.getPlayer(name) == null) {
        p.sendMessage(this.lf.getString("FACTION_TARGET_NOT_IN_FACTION").replace("{player}", name));

        return;
      }
      UUID uuid = playerFaction.getPlayer(name).getUniqueId();

      if (playerFaction.getOfficers().contains(uuid) && playerFaction.getOfficers().contains(p.getUniqueId())) {
        p.sendMessage(this.lf.getString("FACTION_CANNOT_KICK_OTHER_OFFICERS"));

        return;
      }
      if (playerFaction.isLeader(uuid)) {
        p.sendMessage(this.lf.getString("FACTION_CANNOT_KICK_LEADER"));

        return;
      }
      if (playerFaction.isFrozen() && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
        p.sendMessage(this.lf.getString("FACTION_KICK_FROZEN"));

        return;
      }
      playerFaction.sendMessage(this.lf.getString("FACTION_PLAYER_KICKED").replace("{player}", Bukkit.getOfflinePlayer(uuid).getName()));

      if (playerFaction.getOfficers().contains(uuid)) {
        playerFaction.getOfficers().remove(uuid);
      } else {
        playerFaction.getMembers().remove(uuid);
      }
      return;
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.KICK"));
  }
}


