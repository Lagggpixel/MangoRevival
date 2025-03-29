package me.lagggpixel.mango.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;


public class PlayerUtils {
  public static ArrayList<Player> getOnlinePlayers() {
    ArrayList<Player> list = new ArrayList<>();
    for (World world : Bukkit.getWorlds()) {
      for (Player online : world.getPlayers()) {
        if (online != null) {
          list.add(online);
        }
      }
    }
    return list;
  }

  public static boolean hasInventorySpace(Inventory inventory, ItemStack is) {
    Inventory inv = Bukkit.createInventory(null, inventory.getSize());

    for (int i = 0; i < inv.getSize(); i++) {
      if (inventory.getItem(i) != null) {
        ItemStack item = inventory.getItem(i).clone();
        inv.setItem(i, item);
      }
    }

    return (inv.addItem(new ItemStack[]{is.clone()}).size() == 0);
  }


  public static int checkSlotsAvailable(Inventory inv) {
    ItemStack[] items = inv.getContents();
    int emptySlots = 0;

    for (ItemStack is : items) {
      if (is == null) {
        emptySlots++;
      }
    }

    return emptySlots;
  }

  public static void healPlayer(Player target) {
    if (target == null) {
      return;
    }
    target.setHealth(20);
    target.setFoodLevel(20);
    target.setSaturation(0);
  }

  public static void clearEffects(Player player) {
    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
      player.removePotionEffect(activePotionEffect.getType());
    }
  }
}


