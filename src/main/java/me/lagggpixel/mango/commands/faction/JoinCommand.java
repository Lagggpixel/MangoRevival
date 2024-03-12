package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;

import java.util.List;


public class JoinCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public JoinCommand() {
    super("join", List.of("accept"));
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

      try {
        if (this.fm.getFactionByName(name) == null) {
          if (this.fm.getFactionByPlayerName(name) != null) {
            PlayerFaction faction = this.fm.getFactionByPlayerName(name);
            if (faction.getPlayers().size() >= this.cf.getInt("Faction.Max-Players") && !p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
              p.sendMessage(this.lf.getString("FACTION_TOO_MANY_PLAYERS"));
              return;
            }
            if (faction.getInvitedPlayers().contains(p.getUniqueId()) || p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
              p.sendMessage(this.lf.getString("FACTION_JOINED_PLAYER").replace("{faction}", faction.getName()));
              faction.sendMessage(this.lf.getString("FACTION_JOINED_FACTION").replace("{player}", p.getName()));
              faction.getMembers().add(p.getUniqueId());
              faction.getInvitedPlayers().remove(p.getUniqueId());

              return;
            }
          }
        } else {
          PlayerFaction faction = (PlayerFaction) this.fm.getFactionByName(name);
          if (faction.getPlayers().size() >= this.cf.getInt("Faction.Max-Players") && !p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
            p.sendMessage(this.lf.getString("FACTION_TOO_MANY_PLAYERS"));
            return;
          }
          if (faction.getInvitedPlayers().contains(p.getUniqueId()) || p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
            p.sendMessage(this.lf.getString("FACTION_JOINED_PLAYER").replace("{faction}", faction.getName()));
            faction.sendMessage(this.lf.getString("FACTION_JOINED_FACTION").replace("{player}", p.getName()));
            faction.getInvitedPlayers().remove(p.getUniqueId());
            faction.getMembers().add(p.getUniqueId());
            return;
          }
          if (this.fm.getFactionByPlayerName(name) != null && this.fm.getFactionByPlayerName(name) instanceof PlayerFaction) {
            faction = (PlayerFaction) this.fm.getFactionByPlayerName(name);
            assert faction != null;
            if (faction.getPlayers().size() >= this.cf.getInt("Faction.Max-Players") && !p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
              p.sendMessage(this.lf.getString("FACTION_TOO_MANY_PLAYERS"));
              return;
            }
            if (faction.getInvitedPlayers().contains(p.getUniqueId()) || p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".join")) {
              p.sendMessage(this.lf.getString("FACTION_JOINED_PLAYER").replace("{faction}", faction.getName()));
              faction.sendMessage(this.lf.getString("FACTION_JOINED_FACTION").replace("{player}", p.getName()));
              faction.getMembers().add(p.getUniqueId());
              faction.getInvitedPlayers().remove(p.getUniqueId());

              return;
            }
          }
        }
      } catch (Exception e) {
        p.sendMessage(this.lf.getString("FACTION_IS_SYSTEM"));

        return;
      }
      p.sendMessage(this.lf.getString("FACTION_PLAYER_NOT_INVITED"));

      return;
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.JOIN"));
  }
}


