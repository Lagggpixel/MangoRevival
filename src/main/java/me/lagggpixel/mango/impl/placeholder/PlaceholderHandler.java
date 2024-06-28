package me.lagggpixel.mango.impl.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.classes.ClassesHandler;
import me.lagggpixel.mango.commands.faction.HomeCommand;
import me.lagggpixel.mango.commands.faction.StuckCommand;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Lagggpixel
 * @since March 12, 2024
 */
@SuppressWarnings("deprecation")
public class PlaceholderHandler extends PlaceholderExpansion {
  @Override
  public @NotNull String getIdentifier() {
    return "mango";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Lagggpixel";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

    String[] args = params.split("_");

    if (args.length == 0) {
      return null;
    }

    switch (args[0].toLowerCase()) {
      case "faction": {
        if (args.length == 1) {
          return null;
        }
        switch (args[1].toLowerCase()) {
          case "name":
            if (args.length == 2) {
              if (!(offlinePlayer instanceof Player) || offlinePlayer.getPlayer() == null) {
                return null;
              }
              Player player = offlinePlayer.getPlayer();
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "No Faction";
              }
              return faction.getName();
            }
            if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "No Faction";
              }
              return faction.getName();
            }
            return null;

          case "dtr":
            if (args.length == 2) {
              if (!(offlinePlayer instanceof Player) || offlinePlayer.getPlayer() == null) {
                return null;
              }
              Player player = offlinePlayer.getPlayer();
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getDtr());
            }
            if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getDtr());
            }
            return null;
          case "online":
            if (args.length == 2) {
              if (!(offlinePlayer instanceof Player) || offlinePlayer.getPlayer() == null) {
                return null;
              }
              Player player = offlinePlayer.getPlayer();
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getOnlinePlayers().size());
            }
            if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getOnlinePlayers().size());
            }
            return null;
          case "playercount":
            if (args.length == 2) {
              if (!(offlinePlayer instanceof Player) || offlinePlayer.getPlayer() == null) {
                return null;
              }
              Player player = offlinePlayer.getPlayer();
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getPlayers().size());
            }
            if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
              if (faction == null) {
                return "Not In Faction";
              }
              return String.valueOf(faction.getPlayers().size());
            }
            return null;
          default:
            return null;
        }
      }

      case "timer": {
        if (args.length == 1) {
          return null;
        }
        switch (args[1].toLowerCase()) {
          case "home": {
            if (args.length == 2) {
              HomeCommand.Warmup homeWarmup = HomeCommand.getWaiting().get(offlinePlayer.getName());
              if (homeWarmup == null) {
                return "0";
              }
              return String.valueOf(homeWarmup.getSeconds());
            } else if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              HomeCommand.Warmup homeWarmup = HomeCommand.getWaiting().get(player.getName());
              if (homeWarmup == null) {
                return "0";
              }
              return String.valueOf(homeWarmup.getSeconds());
            }
            return null;
          }
          case "stuck": {
            if (args.length == 2) {
              StuckCommand.Warmup stuckWarmup = StuckCommand.getWaiting().get(offlinePlayer.getName());
              if (stuckWarmup == null) {
                return "0";
              }
              return String.valueOf(stuckWarmup.getSeconds());
            } else if (args.length == 3) {
              OfflinePlayer player = Mango.getInstance().getServer().getOfflinePlayer(args[2]);
              StuckCommand.Warmup stuckWarmup = StuckCommand.getWaiting().get(player.getName());
              if (stuckWarmup == null) {
                return "0";
              }
              return String.valueOf(stuckWarmup.getSeconds());
            }
            return null;
          }
          default: {
            return null;
          }
        }
      }

      case "energy": {
        if (args.length > 2) {
          return null;
        }
        if (args .length == 1) {
          Player player = offlinePlayer.getPlayer();
          if (player == null) {
            return null;
          }

          return String.valueOf(ClassesHandler.getPlayerData().get(player).getEnergyRounded());
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
          return null;
        }
        return String.valueOf(ClassesHandler.getPlayerData().get(player).getEnergyRounded());
      }
    }

    return null;
  }
}
