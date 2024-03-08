package me.lagggpixel.mango.runnable;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.enums.Classes;
import me.lagggpixel.mango.events.PlayerClassChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ClassesRunnable extends BukkitRunnable implements Listener {

  private final Map<Player, Classes> playerClasses;

  public ClassesRunnable() {
    Mango.getInstance().getServer().getPluginManager().registerEvents(this, Mango.getInstance());
    this.playerClasses = new HashMap<>();
  }

  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      updatePlayerClassStatus(player);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    playerClasses.remove(event.getPlayer());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    playerClasses.put(event.getPlayer(), null);
    updatePlayerClassStatus(event.getPlayer());
  }

  private void updatePlayerClassStatus(Player player) {
    Classes previousClass = getPlayerClass(player);

    PlayerInventory playerInventory = player.getInventory();
    ItemStack helmet = playerInventory.getHelmet();
    ItemStack chestplate = playerInventory.getChestplate();
    ItemStack leggings = playerInventory.getLeggings();
    ItemStack boots = playerInventory.getBoots();

    if (helmet == null || chestplate == null || leggings == null || boots == null) {
      if (previousClass != null) {
        playerClasses.put(player, null);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerClassChangeEvent(player, previousClass, null));
      }
      return;
    }
    if (!isSameArmourMaterial(helmet, chestplate, leggings, boots)) {
      return;
    }
    Material material = getArmourMaterial(helmet);
    if (material == null) {
      return;
    }
    Classes newClass = switch (material) {
      case LEATHER -> Classes.ARCHER;
      case IRON_INGOT -> Classes.MINER;
      case GOLD_INGOT -> Classes.BARD;
      case DIAMOND -> Classes.DIAMOND;
      case FIRE -> Classes.ROUGE;
      default -> null;
    };
    if (newClass == null) {
      if (previousClass != null) {
        playerClasses.put(player, null);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerClassChangeEvent(player, previousClass, null));
      }
      return;
    }
    if (previousClass == newClass) {
      return;
    }
    playerClasses.put(player, newClass);
    Bukkit.getServer().getPluginManager().callEvent(new PlayerClassChangeEvent(player, previousClass, newClass));
  }

  private Classes getPlayerClass(Player player) {
    if (!playerClasses.containsKey(player)) {
      return null;
    }
    return playerClasses.get(player);
  }

  private boolean isSameArmourMaterial(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
    Material material = getArmourMaterial(helmet);
    return material != getArmourMaterial(chestplate) && material == getArmourMaterial(leggings) && material == getArmourMaterial(boots);
  }

  private Material getArmourMaterial(ItemStack itemStack) {
    Material material = itemStack.getType();

    if (material == Material.LEATHER_HELMET) {
      return Material.LEATHER;
    }
    if (material == Material.LEATHER_CHESTPLATE) {
      return Material.LEATHER;
    }
    if (material == Material.LEATHER_LEGGINGS) {
      return Material.LEATHER;
    }
    if (material == Material.LEATHER_BOOTS) {
      return Material.LEATHER;
    }

    if (material == Material.IRON_HELMET) {
      return Material.IRON_INGOT;
    }
    if (material == Material.IRON_CHESTPLATE) {
      return Material.IRON_INGOT;
    }
    if (material == Material.IRON_LEGGINGS) {
      return Material.IRON_INGOT;
    }
    if (material == Material.IRON_BOOTS) {
      return Material.IRON_INGOT;
    }

    if (material == Material.GOLDEN_HELMET) {
      return Material.GOLD_INGOT;
    }
    if (material == Material.GOLDEN_CHESTPLATE) {
      return Material.GOLD_INGOT;
    }
    if (material == Material.GOLDEN_LEGGINGS) {
      return Material.GOLD_INGOT;
    }
    if (material == Material.GOLDEN_BOOTS) {
      return Material.GOLD_INGOT;
    }

    if (material == Material.DIAMOND_HELMET) {
      return Material.DIAMOND;
    }
    if (material == Material.DIAMOND_CHESTPLATE) {
      return Material.DIAMOND;
    }
    if (material == Material.DIAMOND_LEGGINGS) {
      return Material.DIAMOND;
    }
    if (material == Material.DIAMOND_BOOTS) {
      return Material.DIAMOND;
    }

    if (material == Material.CHAINMAIL_HELMET) {
      return Material.FIRE;
    }
    if (material == Material.CHAINMAIL_CHESTPLATE) {
      return Material.FIRE;
    }
    if (material == Material.CHAINMAIL_LEGGINGS) {
      return Material.FIRE;
    }
    if (material == Material.CHAINMAIL_BOOTS) {
      return Material.FIRE;
    }

    return null;

  }
}
