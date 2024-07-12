package me.lagggpixel.mango.classes;

import com.cryptomorin.xseries.XMaterial;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
              applyEffect(player, k, v);
            });
          }

          Classes classes = cPlayer.getClasses();
          if (classes != null) {
            switch (classes) {
              case BARD: {
                checkBard(player);
              }
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
    applyTeamEffect(p, effect, amplifier, 45);
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
    applyEffect(p, effect, amplifier, ticks);
    PlayerFaction faction = Mango.getInstance().getFactionManager().getFaction(p);
    if (faction == null) {
      return;
    }
    for (Player factionPlayer : faction.getOnlinePlayers()) {
      double distance = p.getLocation().distance(factionPlayer.getLocation());
      if (distance > 15) {
        continue;
      }
      applyEffect(factionPlayer, effect, amplifier, ticks);
    }
  }

  /**
   * Applies a effect to the given player for 40 ticks
   *
   * @param p         the player to apply the effect to
   * @param effect    the type of potion effect to apply
   * @param amplifier the strength of the potion effect
   */
  public static void applyEffect(Player p, PotionEffectType effect, int amplifier) {
    applyEffect(p, effect, amplifier, 45);
  }

  /**
   * Applies a effect to the given player
   *
   * @param p         the player to apply the effect to
   * @param effect    the potion effect type to apply
   * @param amplifier the amplifier for the potion effect
   * @param ticks     the duration of the potion effect in ticks
   */
  public static void applyEffect(@NotNull Player p, PotionEffectType effect, int amplifier, int ticks) {
    if (p.hasPotionEffect(effect)) {
      for (PotionEffect potionEffect : p.getActivePotionEffects().stream().filter(potionEffect ->
          potionEffect.getType() == effect).collect(Collectors.toList())) {
        if (potionEffect.getAmplifier() > amplifier) {
          return;
        }
        if (potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= ticks) {
          return;
        }
      }
    }
    p.addPotionEffect(effect.createEffect(ticks, amplifier), true);
  }

  private void checkBard(@NotNull Player player) {
    ItemStack itemStack = player.getInventory().getItemInHand();
    switch (XMaterial.matchXMaterial(itemStack.getType())) {
      case MAGMA_CREAM: {
        applyTeamEffect(player, PotionEffectType.FIRE_RESISTANCE, 1);
        break;
      }
      case GOLDEN_CARROT: {
        applyTeamEffect(player, PotionEffectType.NIGHT_VISION, 1);
        break;
      }
      case SUGAR: {
        applyTeamEffect(player, PotionEffectType.SPEED, 2);
        break;
      }
      case BLAZE_POWDER: {
        applyTeamEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
        break;
      }
      case GHAST_TEAR: {
        applyTeamEffect(player, PotionEffectType.REGENERATION, 1);
        break;
      }
      case FEATHER: {
        applyTeamEffect(player, PotionEffectType.JUMP, 2);
        break;
      }
      case IRON_INGOT: {
        applyTeamEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
        break;
      }
    }
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
