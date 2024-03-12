package me.lagggpixel.mango.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Lagggpixel
 * @since March 12, 2024
 */
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

    if (args[0].equalsIgnoreCase("faction")) {
      if (args.length == 1) {
        if (!(offlinePlayer instanceof Player player)) {
          return null;
        }
        Faction faction = Mango.getInstance().getFactionManager().getFaction(player);
        if (faction == null) {
          return "No Faction";
        }
        return faction.getName();
      }
      if (args.length == 2) {
        Player player = Mango.getInstance().getServer().getPlayer(args[1]);
        if (player == null) {
          return "Player Not Online";
        }
        Faction faction = Mango.getInstance().getFactionManager().getFaction(player);
        if (faction == null) {
          return "No Faction";
        }
        return faction.getName();
      }
      return null;
    }

    if (args[0].equalsIgnoreCase("dtr")) {
      if (args.length == 1) {
        if (!(offlinePlayer instanceof Player player)) {
          return null;
        }
        PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
        if (faction == null) {
          return "Not In Faction";
        }
        return String.valueOf(faction.getDtr());
      }
      if (args.length == 2) {
        Player player = Mango.getInstance().getServer().getPlayer(args[1]);
        if (player == null) {
          return "Player Not Online";
        }
        PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(player);
        if (faction == null) {
          return "Not In Faction";
        }
        return String.valueOf(faction.getDtr());
      }
      return null;
    }

    return null;
  }
}
