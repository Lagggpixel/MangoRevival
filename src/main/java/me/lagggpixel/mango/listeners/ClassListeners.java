package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.classes.Classes;
import me.lagggpixel.mango.classes.ClassesHandler;
import me.lagggpixel.mango.classes.cPlayer;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.impl.glaedr.scoreboards.Entry;
import me.lagggpixel.mango.impl.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
  public void RougeBackStabEvent(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }
    Player damager = (Player) event.getDamager();
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) event.getEntity();
    cPlayer damagerData = ClassesHandler.getPlayerData().get(damager);
    if (damagerData.getClasses() != Classes.ROUGE) {
      return;
    }
    cPlayer victimData = ClassesHandler.getPlayerData().get(victim);
    if (victimData.getClasses() != Classes.DIAMOND) {
      return;
    }
    if (damager.getItemInHand().getType() != Material.GOLD_SWORD) {
      return;
    }
    float yaw1 = victim.getLocation().getYaw();
    float yaw2 = damager.getLocation().getYaw();
    if (Math.abs(yaw2 - yaw1) > 75) {
      return;
    }
    event.setDamage(6);
    damager.setItemInHand(null);
    damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1, 1);
  }

  /**
   * A method that triggers when a player's arrow hits another entity
   *
   * @param event the EntityDamageByEntityEvent triggered when the player's arrow hits another entity
   */

  @EventHandler
  public void onPlayerHitByArrow(EntityDamageByEntityEvent event) {

    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) event.getEntity();

    if (!(event.getDamager() instanceof Arrow)) {
      return;
    }
    Arrow arrow = (Arrow) event.getDamager();

    if (!(arrow.getShooter() instanceof Player)) {
      return;
    }
    Player damager = (Player) arrow.getShooter();

    if (ClassesHandler.getPlayerData().get(damager).getClasses() != Classes.ARCHER) {
      return;
    }

    if (ClassesHandler.getPlayerData().get(victim).getClasses() != Classes.DIAMOND) {
      victim.damage(4, damager);
      return;
    }

    if (!ClassesHandler.getPlayerData().get(victim).isArcherTagged()) {
      ClassesHandler.getPlayerData().get(victim).setArcherTagged(true);
      ClassesHandler.getPlayerData().get(victim).setArcherTagTimer(5 * 20L);
      PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(victim);
      new Entry("stuck", scoreboard).setText(this.cf.getString("Scoreboard.Archer-Tagged")).setCountdown(true).setTime(this.cf.getInt("Classes.Archer-Tag-Time", 5)).send();
    }
  }

  /**
   * A method that triggers when a player's gets hit by an entity
   * Archer tagged players will have the damage increased by 1.25
   *
   * @param event the EntityDamageByEntityEvent triggered when the player's arrow hits another entity
   */
  @EventHandler
  public void onPlayerHitWhenTagged(EntityDamageByEntityEvent event) {

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player victim = (Player) event.getEntity();

    if (!ClassesHandler.getPlayerData().get(victim).isArcherTagged()) {
      return;
    }

    event.setDamage(event.getDamage() * 1.25);
  }

  /**
   * A method that triggers when a player right clicks with an item, applying various archer effects based on the item held.
   *
   * @param event the PlayerInteractEvent triggered when the player interacts
   */
  @EventHandler()
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Classes classes = ClassesHandler.getPlayerData().get(player).getClasses();
    if (classes == null) {
      return;
    }
    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }

    // For 1.9+ checking hand
    try {
      Class<? extends PlayerInteractEvent> clazz = event.getClass();
      Method method = clazz.getMethod("getHand");
      EquipmentSlot value = (EquipmentSlot) method.invoke(clazz);
      if (value != EquipmentSlot.HAND) {
        return;
      }
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
      // Not on 1.9+
    }

    ItemStack itemStack = player.getInventory().getItemInHand();

    switch (classes) {
      case BARD: {
        switch (itemStack.getType()) {
          case BLAZE_POWDER: {
            if (!forceApplyBardEffect(player, PotionEffectType.INCREASE_DAMAGE, 2, 40)) {
              return;
            }
            break;
          }
          case IRON_INGOT: {
            if (!forceApplyBardEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 3, 40)) {
              return;
            }
            break;
          }
          case FEATHER: {
            if (!forceApplyBardEffect(player, PotionEffectType.JUMP, 4, 20)) {
              return;
            }
            break;
          }
          case SUGAR: {
            if (!forceApplyBardEffect(player, PotionEffectType.SPEED, 3, 30)) {
              return;
            }
            break;
          }
          default: {
            return;
          }
        }

        itemStack.setAmount(itemStack.getAmount() - 1);
        break;
      }

      case ARCHER: {
        switch (itemStack.getType()) {
          case SUGAR: {
            if (!forceApplyArcherEffect(player, PotionEffectType.SPEED, 4, 50)) {
              return;
            }
            break;
          }
          case FEATHER: {
            if (!forceApplyArcherEffect(player, PotionEffectType.JUMP, 4, 40)) {
              return;
            }
            break;
          }
          default: {
            return;
          }
        }
        itemStack.setAmount(itemStack.getAmount() - 1);
        break;
      }
    }
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean forceApplyArcherEffect(Player player, PotionEffectType effect, int amplifier, int energy) {
    cPlayer cPlayer = ClassesHandler.getPlayerData().get(player);
    if (!cPlayer.hasEnergy(energy)) {
      player.sendMessage(lf.getString("CLASSES.NOT_ENOUGH_ENERGY").replace("{amount_required}", String.valueOf(energy)).replace("{current_energy}", String.valueOf(cPlayer.getEnergyRounded())));
      return false;
    }
    cPlayer.removeEnergy(energy);
    ClassesHandler.applySelfEffect(player, effect, amplifier, 20 * 5);
    return true;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean forceApplyBardEffect(Player player, PotionEffectType effect, int amplifier, int energy) {
    cPlayer cPlayer = ClassesHandler.getPlayerData().get(player);
    if (!cPlayer.hasEnergy(energy)) {
      player.sendMessage(lf.getString("CLASSES.NOT_ENOUGH_ENERGY").replace("{amount_required}", String.valueOf(energy)).replace("{current_energy}", String.valueOf(cPlayer.getEnergyRounded())));
      return false;
    }
    cPlayer.removeEnergy(energy);
    ClassesHandler.applyTeamEffect(player, effect, amplifier, 20 * 5);
    return true;
  }

  /**
   * A method that triggers when a player's held item changes, applying various bard effects based on the item held.
   *
   * @param event the PlayerItemHeldEvent triggered when the player's held item changes
   */
  @EventHandler
  public void onPlayerItemInHandChange(PlayerItemHeldEvent event) {
    Classes classes = ClassesHandler.getPlayerData().get(event.getPlayer()).getClasses();
    if (classes == Classes.BARD) {
      Player player = event.getPlayer();
      ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
      if (itemStack == null) {
        return;
      }
      switch (itemStack.getType()) {
        case MAGMA_CREAM: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.FIRE_RESISTANCE, 1);
          break;
        }
        case GOLDEN_CARROT: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.NIGHT_VISION, 1);
          break;
        }
        case SUGAR: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.SPEED, 2);
          break;
        }
        case BLAZE_POWDER: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
          break;
        }
        case GHAST_TEAR: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.REGENERATION, 1);
          break;
        }
        case FEATHER: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.JUMP, 2);
          break;
        }
        case IRON_INGOT: {
          ClassesHandler.applyTeamEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
          break;
        }
      }
    }
  }
}
