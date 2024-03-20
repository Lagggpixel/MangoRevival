package me.lagggpixel.mango.classes;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.impl.glaedr.scoreboards.Entry;
import me.lagggpixel.mango.impl.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ClassesHandler implements Listener {

  private static final Map<Player, cPlayer> playerData = new HashMap<>();
  private final LanguageFile lf;
  private final ConfigFile cf;

  public ClassesHandler() {
    Mango.getInstance().getServer().getPluginManager().registerEvents(this, Mango.getInstance());
    lf = Mango.getInstance().getLanguageFile();
    cf = Mango.getInstance().getConfigFile();
    new BukkitRunnable() {
      @Override
      public void run() {
        playerData.forEach((player, cPlayer) -> cPlayer.refreshData());
      }
    }.runTaskTimerAsynchronously(Mango.getInstance(), 1, 1);

    new BukkitRunnable() {
      @Override
      public void run() {
        playerData.forEach((player, cPlayer) -> {
          Classes classes = cPlayer.getClasses();
          if (classes == Classes.BARD) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            switch (itemStack.getType()) {
              case MAGMA_CREAM -> applyBardEffect(player, PotionEffectType.FIRE_RESISTANCE, 1);
              case GOLDEN_CARROT -> applyBardEffect(player, PotionEffectType.NIGHT_VISION, 1);
              case SUGAR -> applyBardEffect(player, PotionEffectType.SPEED, 2);
              case BLAZE_POWDER -> applyBardEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
              case GHAST_TEAR -> applyBardEffect(player, PotionEffectType.REGENERATION, 1);
              case FEATHER -> applyBardEffect(player, PotionEffectType.JUMP, 2);
              case IRON_INGOT -> applyBardEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
            }
          }
        });
      }
    }.runTaskTimer(Mango.getInstance(), 20, 20);
  }

  /**
   * Apply a Bard effect to the player and nearby faction members for 40 ticks
   *
   * @param p         the player to apply the effect to
   * @param effect    the type of potion effect to apply
   * @param amplifier the strength of the potion effect
   */
  public void applyBardEffect(Player p, PotionEffectType effect, int amplifier) {
    applyBardEffect(p, effect, amplifier, 40);
  }

  /**
   * Applies a Bard effect to the given player and their faction members.
   *
   * @param p         the player to apply the effect to
   * @param effect    the potion effect type to apply
   * @param amplifier the amplifier for the potion effect
   * @param ticks     the duration of the potion effect in ticks
   */
  public void applyBardEffect(Player p, PotionEffectType effect, int amplifier, int ticks) {
    PlayerFaction f = Mango.getInstance().getFactionManager().getFaction(p);
    if (!(p.hasPotionEffect(effect)
        || p.getPotionEffect(effect) == null
        || Objects.requireNonNull(p.getPotionEffect(effect)).getAmplifier() >= amplifier)) {
      p.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
    }
    if (f == null) {
      return;
    }
    for (Player factionPlayer : f.getOnlinePlayers()) {
      double distance = p.getLocation().distance(factionPlayer.getLocation());
      if (distance > 15) {
        continue;
      }
      if (factionPlayer.hasPotionEffect(effect)
          || factionPlayer.getPotionEffect(effect) == null
          || Objects.requireNonNull(factionPlayer.getPotionEffect(effect)).getAmplifier() >= amplifier) {
        continue;
      }
      factionPlayer.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
    }
  }

  /**
   * Applies a Archer effect to the given player for 40 ticks
   *
   * @param p         the player to apply the effect to
   * @param effect    the type of potion effect to apply
   * @param amplifier the strength of the potion effect
   */
  public void applyArcherEffect(Player p, PotionEffectType effect, int amplifier) {
    applyArcherEffect(p, effect, amplifier, 40);
  }

  /**
   * Applies a Archer effect to the given player
   *
   * @param p         the player to apply the effect to
   * @param effect    the potion effect type to apply
   * @param amplifier the amplifier for the potion effect
   * @param ticks     the duration of the potion effect in ticks
   */
  public void applyArcherEffect(Player p, PotionEffectType effect, int amplifier, int ticks) {
    if (p.hasPotionEffect(effect)
        || p.getPotionEffect(effect) == null
        || Objects.requireNonNull(p.getPotionEffect(effect)).getAmplifier() >= amplifier) {
      return;
    }
    p.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
  }

  /**
   * Listener for player join events to update player data
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    playerData.put(event.getPlayer(), new cPlayer(event.getPlayer()));
  }

  /**
   * Listener for player quit events to update player data
   */
  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    playerData.remove(event.getPlayer());
  }

  /**
   * A method that triggers when a player's held item changes, applying various bard effects based on the item held.
   *
   * @param event the PlayerItemHeldEvent triggered when the player's held item changes
   */
  @EventHandler
  public void onPlayerItemInHandChange(PlayerItemHeldEvent event) {
    Classes classes = playerData.get(event.getPlayer()).getClasses();
    if (classes == Classes.BARD) {
      Player player = event.getPlayer();
      ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
      if (itemStack == null) {
        return;
      }
      switch (itemStack.getType()) {
        case MAGMA_CREAM -> applyBardEffect(player, PotionEffectType.FIRE_RESISTANCE, 1);
        case GOLDEN_CARROT -> applyBardEffect(player, PotionEffectType.NIGHT_VISION, 1);
        case SUGAR -> applyBardEffect(player, PotionEffectType.SPEED, 2);
        case BLAZE_POWDER -> applyBardEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
        case GHAST_TEAR -> applyBardEffect(player, PotionEffectType.REGENERATION, 1);
        case FEATHER -> applyBardEffect(player, PotionEffectType.JUMP, 2);
        case IRON_INGOT -> applyBardEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
      }
    }
  }


  /**
   * A method that triggers when a player right clicks with an item, applying various archer effects based on the item held.
   *
   * @param event the PlayerInteractEvent triggered when the player interacts
   */
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Classes classes = playerData.get(player).getClasses();
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }
    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }
    ItemStack itemStack = player.getInventory().getItemInMainHand();
    if (classes == Classes.ARCHER) {
      switch (itemStack.getType()) {
        case SUGAR -> forceApplyArcherEffect(player, PotionEffectType.SPEED, 3, 50);
        case FEATHER -> forceApplyArcherEffect(player, PotionEffectType.JUMP, 4, 40);
      }
    }
    if (classes == Classes.BARD) {
      switch (itemStack.getType()) {
        case BLAZE_POWDER -> forceApplyBardEffect(player, PotionEffectType.INCREASE_DAMAGE, 2, 40);
        case IRON_INGOT -> forceApplyBardEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 3, 40);
        case FEATHER -> forceApplyBardEffect(player, PotionEffectType.JUMP, 4, 20);
        case SUGAR -> forceApplyBardEffect(player, PotionEffectType.SPEED, 3, 30);
      }
    }
  }

  private void forceApplyArcherEffect(Player player, PotionEffectType effect, int amplifier, int energy) {
    cPlayer cPlayer = playerData.get(player);
    if (!cPlayer.hasEnergy(energy)) {
      player.sendMessage(lf.getString("NOT_ENOUGH_ENERGY").replace("{amount}", String.valueOf(energy)));
    }
    cPlayer.removeEnergy(energy);
    applyArcherEffect(player, effect, amplifier);
  }

  private void forceApplyBardEffect(Player player, PotionEffectType effect, int amplifier, int energy) {
    cPlayer cPlayer = playerData.get(player);
    if (!cPlayer.hasEnergy(energy)) {
      player.sendMessage(lf.getString("NOT_ENOUGH_ENERGY").replace("{amount}", String.valueOf(energy)));
    }
    cPlayer.removeEnergy(energy);
    applyBardEffect(player, effect, amplifier);
  }

  /**
   * A method that triggers when a player's arrow hits another entity
   *
   * @param event the EntityDamageByEntityEvent triggered when the player's arrow hits another entity
   */
  @EventHandler
  public void onPlayerHitByArrow(EntityDamageByEntityEvent event) {

    if (!(event.getEntity() instanceof Player victim)) {
      return;
    }

    if (!(event.getDamager() instanceof Arrow arrow)) {
      return;
    }

    if (!(arrow.getShooter() instanceof Player damager)) {
      return;
    }


    if (playerData.get(damager).getClasses() != Classes.ARCHER) {
      return;
    }

    if (playerData.get(victim).getClasses() != Classes.DIAMOND) {
      victim.damage(4, damager);
      return;
    }

    if (!playerData.get(victim).isArcherTagged()) {
      playerData.get(victim).setArcherTagged(true);
      playerData.get(victim).setArcherTagTimer(5 * 20L);
      PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(victim);
      new Entry("stuck", scoreboard).setText(this.cf.getString("Scoreboard.Archer-Tagged")).setCountdown(true).setTime(this.cf.getInt("Classes.Archer-Tag-Time", 5)).send();
    }
  }

  public void onPlayerHitWhenTagged(EntityDamageByEntityEvent event) {

    if (!(event.getEntity() instanceof Player victim)) {
      return;
    }

    if (!playerData.get(victim).isArcherTagged()) {
      return;
    }

    event.setDamage(event.getDamage() * 1.25);
  }
}
