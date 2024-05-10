package me.lagggpixel.mango.classes;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ClassesHandler implements Listener {

  @Getter
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
          if (cPlayer.getClasses() != null) {
            cPlayer.getClasses().getClassEffects().forEach((k, v) -> {
              player.addPotionEffect(new PotionEffect(k, 40, v));
            });
          }

          Classes classes = cPlayer.getClasses();
          if (classes == Classes.BARD) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            switch (itemStack.getType()) {
              case MAGMA_CREAM -> applyTeamEffect(player, PotionEffectType.FIRE_RESISTANCE, 1);
              case GOLDEN_CARROT -> applyTeamEffect(player, PotionEffectType.NIGHT_VISION, 1);
              case SUGAR -> applyTeamEffect(player, PotionEffectType.SPEED, 2);
              case BLAZE_POWDER -> applyTeamEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
              case GHAST_TEAR -> applyTeamEffect(player, PotionEffectType.REGENERATION, 1);
              case FEATHER -> applyTeamEffect(player, PotionEffectType.JUMP, 2);
              case IRON_INGOT -> applyTeamEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
            }
          }
        });
      }
    }.runTaskTimer(Mango.getInstance(), 20, 20);
  }

  /**
   * Apply an effect to the player and nearby faction members for 40 ticks
   *
   * @param p         the player to apply the effect to
   * @param effect    the type of potion effect to apply
   * @param amplifier the strength of the potion effect
   */
  public static void applyTeamEffect(Player p, PotionEffectType effect, int amplifier) {
    applyTeamEffect(p, effect, amplifier, 40);
  }

  /**
   * Applies an effect to the given player and their faction members.
   *
   * @param p         the player to apply the effect to
   * @param effect    the potion effect type to apply
   * @param amplifier the amplifier for the potion effect
   * @param ticks     the duration of the potion effect in ticks
   */
  public static void applyTeamEffect(Player p, PotionEffectType effect, int amplifier, int ticks) {
    applySelfEffect(p, effect, amplifier, ticks);
    PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(p);
    if (faction == null) {
      return;
    }
    for (Player factionPlayer : faction.getOnlinePlayers()) {
      double distance = p.getLocation().distance(factionPlayer.getLocation());
      if (distance > 15) {
        continue;
      }
      if (factionPlayer.hasPotionEffect(effect)
          && factionPlayer.getPotionEffect(effect) != null
          && Objects.requireNonNull(factionPlayer.getPotionEffect(effect)).getAmplifier() > amplifier) {
        continue;
      }
      factionPlayer.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
    }
  }

  /**
   * Applies a effect to the given player for 40 ticks
   *
   * @param p         the player to apply the effect to
   * @param effect    the type of potion effect to apply
   * @param amplifier the strength of the potion effect
   */
  public static void applySelfEffect(Player p, PotionEffectType effect, int amplifier) {
    applySelfEffect(p, effect, amplifier, 40);
  }

  /**
   * Applies a effect to the given player
   *
   * @param p         the player to apply the effect to
   * @param effect    the potion effect type to apply
   * @param amplifier the amplifier for the potion effect
   * @param ticks     the duration of the potion effect in ticks
   */
  public static void applySelfEffect(Player p, PotionEffectType effect, int amplifier, int ticks) {
    if (p.hasPotionEffect(effect)
        && p.getPotionEffect(effect) != null
        && Objects.requireNonNull(p.getPotionEffect(effect)).getAmplifier() > amplifier) {
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





}
