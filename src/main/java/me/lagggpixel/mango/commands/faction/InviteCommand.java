package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;


public class InviteCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public InviteCommand() {
    super("invite", Collections.singletonList("inv"));
  }


  public void execute(Player p, String[] args) {
    if (args.length >= 1) {

      if (this.fm.getFaction(p) == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

        return;
      }
      if (this.fm.getFaction(p) != null) {
        PlayerFaction faction = this.fm.getFaction(p);
        assert faction != null;
        if (faction.getOfficers().contains(p.getUniqueId()) || faction.isLeader(p.getUniqueId()) || p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".invite")) {

          StringBuilder sb = new StringBuilder();
          for (String arg : args) {
            sb.append(arg).append(" ");
          }
          String name = sb.toString().trim().replace(" ", "");

          Player inv = Bukkit.getPlayer(name);

          if (inv == null) {
            p.sendMessage(this.lf.getString("FACTION_PLAYER_NOT_FOUND").replace("{player}", name));

            return;
          }

          if (faction.getPlayers().contains(Bukkit.getOfflinePlayer(inv.getUniqueId()))) {
            p.sendMessage(this.lf.getString("FACTION_PLAYER_ALREADY_JOINED").replace("{player}", name));

            return;
          }
          if (faction.getInvitedPlayers().contains(inv.getUniqueId())) {
            p.sendMessage(this.lf.getString("FACTION_PLAYER_ALREADY_INVITED").replace("{player}", name));


            return;
          }

          faction.sendMessage(this.lf.getString("FACTION_PLAYER_INVITED_FACTION").replace("{inviter}", p.getName()).replace("{invited}", inv.getName()));
          inv.sendMessage(this.lf.getString("FACTION_PLAYER_INVITE_FACTION").replace("{inviter}", p.getName()).replace("{faction}", faction.getName()));
          faction.getInvitedPlayers().add(inv.getUniqueId());
        } else {

          p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
        }


        return;
      }
    }

    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.INVITE"));
  }
}


