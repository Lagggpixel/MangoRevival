package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.enums.Classes;
import me.lagggpixel.mango.runnable.ArcherTagRunnable;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Lagggpixel
 * @since March 12, 2024
 */
@SuppressWarnings("UnstableApiUsage")
public class ClassesListeners implements Listener {

  private final LanguageFile lf;

  public ClassesListeners() {
    lf = Mango.getInstance().getLanguageFile();
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());
  }

  // Check for archer class shooting a diamond class
  @EventHandler
  public void onPlayerDamage(EntityDamageByEntityEvent e) {
    if (!(e.getEntity() instanceof Player victim)) {
      return;
    }
    if (!(e.getDamager() instanceof Arrow arrow)) {
      return;
    }
    if (!(arrow.getShooter() instanceof Player attacker)) {
      return;
    }

    Classes attackerClass = Mango.getInstance().getClassesRunnable().getPlayerClass(attacker);
    if (attackerClass != Classes.ARCHER) {
      return;
    }
    Classes victimClass = Mango.getInstance().getClassesRunnable().getPlayerClass(victim);
    if (victimClass != Classes.DIAMOND) {
      return;
    }
    e.setDamage(0);
    victim.damage(4, DamageSource.builder(DamageType.ARROW).withCausingEntity(attacker).build());
    ArcherTagRunnable.addArcherTag(victim);
    victim.sendMessage(lf.getString("CLASSES.ARCHER_TAG.DIAMOND").replace("{player}", attacker.getName()));
    attacker.sendMessage(lf.getString("CLASSES.ARCHER_TAG.ARCHER").replace("{player}", victim.getName()));
  }

}