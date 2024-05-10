package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.classes.Classes;
import me.lagggpixel.mango.classes.ClassesHandler;
import me.lagggpixel.mango.classes.cPlayer;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Lagggpixel
 * @since May 09, 2024
 */
public class ClassListeners implements Listener {

  private final Mango main = Mango.getInstance();
  private final LanguageFile lf = this.main.getLanguageFile();
  private final ConfigFile cf = this.main.getConfigFile();
  private final ClassesHandler ch = this.main.getClassesHandler();

  public ClassListeners() {
    Bukkit.getPluginManager().registerEvents(this, this.main);
  }

  /**
   * Listener for rouge back-stabs
   */
  @EventHandler
  public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }
    Player damager = (Player) event.getDamager();
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) event.getEntity();
    cPlayer damagerData =ClassesHandler.getPlayerData().get(damager);
    if (damagerData.getClasses() != Classes.ROUGE) {
      return;
    }
    cPlayer victimData = ClassesHandler.getPlayerData().get(victim);
    if (victimData.getClasses() != Classes.DIAMOND) {
      return;
    }
    if (damager.getInventory().getItemInMainHand().getType() != Material.GOLDEN_SWORD) {
      return;
    }
    float yaw1 = victim.getLocation().getYaw();
    float yaw2 = damager.getLocation().getYaw();
    if (Math.abs(yaw2 - yaw1) > 75) {
      return;
    }
    event.setDamage(6);
    damager.getInventory().setItemInMainHand(null);
    damager.playSound(damager.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
  }
}
