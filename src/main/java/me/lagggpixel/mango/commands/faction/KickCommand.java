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
      if (!(playerFaction instanceof PlayerFaction)) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));

        return;
      }
      PlayerFaction playerFaction1 = playerFaction;

      if (!playerFaction1.isLeader(p.getUniqueId()) && !playerFaction1.getOfficers().contains(p.getUniqueId())) {
        p.sendMessage(this.lf.getString("FACTION_MUST_BE_LEADER_OR_OFFICER"));

        return;
      }
      StringBuilder sb = new StringBuilder();
      for (String arg : args) {
        sb.append(arg).append(" ");
      }
      String name = sb.toString().trim().replace(" ", "");

      if (playerFaction1.getPlayer(name) == null) {
        p.sendMessage(this.lf.getString("FACTION_TARGET_NOT_IN_FACTION").replace("{player}", name));

        return;
      }
      UUID uuid = playerFaction1.getPlayer(name).getUniqueId();

      if (playerFaction1.getOfficers().contains(uuid) && playerFaction1.getOfficers().contains(p.getUniqueId())) {
        p.sendMessage(this.lf.getString("FACTION_CANNOT_KICK_OTHER_OFFICERS"));

        return;
      }
      if (playerFaction1.isLeader(uuid)) {
        p.sendMessage(this.lf.getString("FACTION_CANNOT_KICK_LEADER"));

        return;
      }
      if (playerFaction1.isFrozen() && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
        p.sendMessage(this.lf.getString("FACTION_KICK_FROZEN"));

        return;
      }
      playerFaction1.sendMessage(this.lf.getString("FACTION_PLAYER_KICKED").replace("{player}", Bukkit.getOfflinePlayer(uuid).getName()));

      if (playerFaction1.getOfficers().contains(uuid)) {
        playerFaction1.getOfficers().remove(uuid);
      } else {
        playerFaction1.getMembers().remove(uuid);
      }
      return;
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.KICK"));
  }
}


