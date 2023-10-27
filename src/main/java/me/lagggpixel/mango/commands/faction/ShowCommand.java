package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class ShowCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ShowCommand() {
    super("show", Arrays.asList(new String[]{"who"}));
  }


  public void execute(Player p, String[] args) {
    if (args.length == 0) {
      if (this.fm.getFaction(p) == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
      } else {
        for (String msg : getInformation((Faction) this.fm.getFaction(p))) {
          p.sendMessage(msg);
        }
      }
      return;
    }
    if (args.length >= 1) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < args.length; i++) {
        sb.append(args[i]).append(" ");
      }
      String name = sb.toString().trim().replace(" ", "");

      if (this.fm.getFaction(name).isEmpty()) {
        p.sendMessage(this.lf.getString("FACTION_NOT_FOUND").replace("{name}", name));

        return;
      }
      for (Faction faction : this.fm.getFaction(name)) {
        for (String msg : getInformation(faction)) {
          p.sendMessage(msg);
        }
      }
    }
  }


  public List<String> getInformation(Faction faction) {
    if (faction instanceof PlayerFaction) {

      List<String> list = new ArrayList<>();
      for (String msg : this.lf.getStringList("FACTION_SHOW")) {
        msg = msg.replace("{faction}", faction.getName());
        msg = msg.replace("{online}", ((PlayerFaction) faction).getOnlinePlayers().size() + "");
        msg = msg.replace("{total}", ((PlayerFaction) faction).getIDs().size() + "");

        if (((PlayerFaction) faction).getHome() != null) {
          msg = msg.replace("{home-coords}", ((PlayerFaction) faction).getHome().getBlockX() + ", " + ((PlayerFaction) faction).getHome().getBlockZ());
        } else {
          msg = msg.replace("{home-coords}", "None");
        }

        String officers = null;
        String members = null;

        for (UUID ofs : ((PlayerFaction) faction).getOfficers()) {
          Player of = Bukkit.getPlayer(ofs);
          if (of != null) {
            if (officers == null) {
              officers = this.cf.getString("ONLINE_COLOR") + of.getName();
              continue;
            }
            officers = addTo(officers, this.cf.getString("ONLINE_COLOR") + of.getName());
            continue;
          }
          OfflinePlayer oof = Bukkit.getOfflinePlayer(ofs);
          if (officers == null) {
            officers = this.cf.getString("OFFLINE_COLOR") + oof.getName();
            continue;
          }
          officers = addTo(officers, this.cf.getString("OFFLINE_COLOR") + oof.getName());
        }


        if (officers != null) officers = officers.replace(" ", this.lf.getString("FACTION_SHOW_SPLITTER") + " ");

        for (UUID mems : ((PlayerFaction) faction).getMembers()) {
          Player mem = Bukkit.getPlayer(mems);
          if (mem != null) {
            if (members == null) {
              members = this.cf.getString("ONLINE_COLOR") + mem.getName();
              continue;
            }
            members = addTo(members, this.cf.getString("ONLINE_COLOR") + mem.getName());
            continue;
          }
          OfflinePlayer omem = Bukkit.getOfflinePlayer(mems);
          if (members == null) {
            members = this.cf.getString("OFFLINE_COLOR") + omem.getName();
            continue;
          }
          members = addTo(members, this.cf.getString("OFFLINE_COLOR") + omem.getName());
        }


        if (members != null) members = members.replace(" ", this.lf.getString("FACTION_SHOW_SPLITTER") + " ");

        if (officers != null) {
          msg = msg.replace("{officers}", officers);
        }
        if (members != null) {
          msg = msg.replace("{members}", members);
        }

        msg = msg.replace("{balance}", ((PlayerFaction) faction).getBalance() + "");

        if (((PlayerFaction) faction).getDtr() < 0.0D) {
          msg = msg.replace("{dtr}", this.cf.getString("RAIDABLE_COLOR") + ((PlayerFaction) faction).getDtr() + this.cf.getString("RAIDABLE_SYMBOL"));
        } else if (((PlayerFaction) faction).isFrozen()) {
          msg = msg.replace("{dtr}", this.cf.getString("FROZEN_COLOR") + ((PlayerFaction) faction).getDtr() + this.cf.getString("FROZEN_SYMBOL"));
        } else if (((PlayerFaction) faction).getDtr() < ((PlayerFaction) faction).getMaxDtr()) {
          msg = msg.replace("{dtr}", this.cf.getString("REGEN_COLOR") + ((PlayerFaction) faction).getDtr() + this.cf.getString("REGEN_SYMBOL"));
        } else {
          msg = msg.replace("{dtr}", this.cf.getString("NOT_RAIDABLE_COLOR") + ((PlayerFaction) faction).getDtr() + this.cf.getString("NOT_RAIDABLE_SYMBOL"));
        }


        Player leader = Bukkit.getPlayer(((PlayerFaction) faction).getLeader());
        if (leader == null) {
          OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(((PlayerFaction) faction).getLeader());
          msg = msg.replace("{leader}", this.cf.getString("OFFLINE_COLOR") + offlinePlayer.getName());
        } else {
          msg = msg.replace("{leader}", this.cf.getString("ONLINE_COLOR") + leader.getName());
        }


        if (((PlayerFaction) faction).isFrozen()) {
          msg = msg.replace("{time}", ((PlayerFaction) faction).getFreezeTime());
        }

        String allies = null;

        if (!((PlayerFaction) faction).getAllies().isEmpty()) {
          for (Faction allyFactions : ((PlayerFaction) faction).getAllies()) {
            if (allies == null) {
              allies = allyFactions.getName();
              continue;
            }
            allies = addTo(allies, allyFactions.getName());
          }


          msg = msg.replace("{allies}", allies);
        }


        if (!msg.contains("{officers}") && !msg.contains("{members}") && !msg.contains("{leader}") && !msg.contains("{home}") && !msg.contains("{allies}") && !msg.contains("{time}")) {
          list.add(msg);
        }
      }

      return list;
    }

    SystemFaction systemFaction = (SystemFaction) faction;
    List<String> information = new ArrayList<>();
    for (String msg : this.lf.getStringList("SYSTEM_SHOW")) {
      if (systemFaction.getHome() != null) {
        msg = msg.replace("{home-coords}", ((SystemFaction) faction).getHome().getBlockX() + ", " + ((SystemFaction) faction).getHome().getBlockZ());
      } else {
        msg = msg.replace("{home-coords}", "None");
      }
      msg = msg.replace("{faction}", faction.getName());
      msg = msg.replace("{deathban}", systemFaction.isDeathban());
      information.add(msg);
    }
    return information;
  }

  public String addTo(String string, String anotherString) {
    String newstring = string + " " + anotherString;
    return ChatColor.translateAlternateColorCodes('&', newstring);
  }
}


