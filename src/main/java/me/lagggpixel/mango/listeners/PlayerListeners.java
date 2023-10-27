package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;


public class PlayerListeners
    implements Listener {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public PlayerListeners() {
    Bukkit.getPluginManager().registerEvents(this, (Plugin) Mango.getInstance());
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    PlayerFaction playerFaction = this.fm.getFaction(p);
    if (playerFaction != null && playerFaction instanceof PlayerFaction) {
      playerFaction.freeze(this.cf.getInt("FREEZE_DURATION"));
      playerFaction.setDtr(playerFaction.getDtrDecimal().subtract(BigDecimal.valueOf(1L)));
      playerFaction.sendMessage(this.lf.getString("PLAYER_EVENTS.DEATH").replace("{player}", p.getName()).replace("{dtr}", playerFaction.getDtrDecimal() + "").replace("{maxdtr}", playerFaction.getMaxDtr() + ""));
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    PlayerFaction playerFaction = this.fm.getFaction(p);
    if (playerFaction != null && playerFaction instanceof PlayerFaction) {
      playerFaction.sendMessage(this.lf.getString("PLAYER_EVENTS.JOIN").replace("{player}", p.getName()));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player) {
      Player p = (Player) e.getEntity();
      for (Claim claim : Mango.getInstance().getClaimManager().getClaims()) {
        if (claim.isInside(p.getLocation(), true) &&
            claim.getOwner() instanceof SystemFaction &&
            !((SystemFaction) claim.getOwner()).isDeathbanBoolean()) {
          e.setCancelled(true);
        }
      }
    }
  }


  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
      Player p = (Player) e.getEntity();
      Player d = (Player) e.getDamager();
      if (this.fm.getFaction(p) != null && this.fm.getFaction(d) != null) {
        if (this.fm.getFaction(p) == this.fm.getFaction(d) && d != p) {
          d.sendMessage(this.lf.getString("FACTION_FRIENDLY_DAMAGE").replace("{player}", p.getName()));
          e.setCancelled(true);
        }
        PlayerFaction playerFaction = this.fm.getFaction(p);
        PlayerFaction damageFaction = this.fm.getFaction(d);
        if (playerFaction.getAllies().contains(damageFaction) || damageFaction.getAllies().contains(playerFaction)) {
          d.sendMessage(this.lf.getString("FACTION_ALLY_DAMAGE").replace("{player}", p.getName()));
          e.setCancelled(true);
        }
      }
    } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
      Player p = (Player) e.getEntity();
      Projectile projectile = (Projectile) e.getDamager();
      if (projectile.getShooter() instanceof Player) {
        Player d = (Player) projectile.getShooter();
        if (this.fm.getFaction(p) != null && this.fm.getFaction(d) != null) {
          if (this.fm.getFaction(p) == this.fm.getFaction(d) && d != p) {
            d.sendMessage(this.lf.getString("FACTION_FRIENDLY_DAMAGE").replace("{player}", p.getName()));
            e.setCancelled(true);
          }
          PlayerFaction playerFaction = this.fm.getFaction(p);
          PlayerFaction damageFaction = this.fm.getFaction(d);
          if (playerFaction.getAllies().contains(damageFaction) || damageFaction.getAllies().contains(playerFaction)) {
            d.sendMessage(this.lf.getString("FACTION_ALLY_DAMAGE").replace("{player}", p.getName()));
            e.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onInteractEnderchest(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENDER_CHEST) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onJoin(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    PlayerFaction playerFaction = this.fm.getFaction(p);
    if (playerFaction != null && playerFaction instanceof PlayerFaction)
      playerFaction.sendMessage(this.lf.getString("PLAYER_EVENTS.QUIT").replace("{player}", p.getName()));
  }
}


