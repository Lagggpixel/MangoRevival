package me.lagggpixel.mango.runnable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lagggpixel
 * @since March 12, 2024
 */
public class ArcherTagRunnable extends BukkitRunnable {

  private static final Map<Player, Long> archerTags = new HashMap<>();

  public ArcherTagRunnable() {
  }

  @Override
  public void run() {
    Map<Player, Long> tempArcherTags = new HashMap<>();
    ArcherTagRunnable.archerTags.forEach((k, v) -> {
      if (v > 0) {
        tempArcherTags.put(k, v - 1);
      } else {
        k.setDisplayName(k.getName());
      }
    });
    ArcherTagRunnable.archerTags.clear();
    ArcherTagRunnable.archerTags.putAll(tempArcherTags);
  }

  public static void addArcherTag(Player player) {
    ArcherTagRunnable.archerTags.remove(player);
    ArcherTagRunnable.archerTags.put(player, 10 * 20L);
    player.setDisplayName(ChatColor.YELLOW + player.getName());
  }

  public static void removeArcherTag(Player player) {
    ArcherTagRunnable.archerTags.remove(player);
    player.setDisplayName(player.getName());
  }

  public static boolean isArcherTagged(Player player) {
    return ArcherTagRunnable.archerTags.containsKey(player);
  }
}
