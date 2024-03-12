package me.lagggpixel.mango.runnable;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.enums.Classes;
import me.lagggpixel.mango.events.PlayerClassChangeEvent;
import org.bukkit.Bukkit;
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

    Classes newClass = Classes.getClassByArmourSet(helmet, chestplate, leggings, boots);
    if (previousClass == newClass) {
      return;
    }
    playerClasses.replace(player, newClass);
    Bukkit.getServer().getPluginManager().callEvent(new PlayerClassChangeEvent(player, previousClass, newClass));
  }

  public Classes getPlayerClass(Player player) {
    if (!playerClasses.containsKey(player)) {
      return null;
    }
    return playerClasses.get(player);
  }
}
