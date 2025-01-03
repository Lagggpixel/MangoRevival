package me.lagggpixel.mango.commands.faction;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.impl.glaedr.scoreboards.Entry;
import me.lagggpixel.mango.impl.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HomeCommand extends FactionSubCommand implements Listener {
  @Getter
  private static final Map<String, Warmup> waiting = new HashMap<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  private static Map<UUID, Integer> homeTimerTicks = new HashMap<>();

  public HomeCommand() {
    super("home", Collections.singletonList("h"));
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());

    if (Mango.getInstance().getGlaedr() == null) {
      BukkitRunnable homeTimerRunnable = new BukkitRunnable() {
        @Override
        public void run() {
          Map<UUID, Integer> newHomeTimer = new HashMap<>();
          homeTimerTicks.forEach((uuid, time) -> {
            if (time - 1 > 0) {
              newHomeTimer.put(uuid, time - 1);
            }
          });
          homeTimerTicks = newHomeTimer;
        }
      };
      homeTimerRunnable.runTaskTimerAsynchronously(Mango.getInstance(), 0L, 1L);
    }
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);

    if (playerFaction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }

    if (waiting.containsKey(p.getName())) {
      p.sendMessage(ChatColor.RED + "You're already teleporting to your faction home!");

      return;
    }
    if (playerFaction.getHome() == null) {
      p.sendMessage(this.lf.getString("FACTION_HOME_NOT_SET"));

      return;
    }
    if (Mango.getInstance().getClaimManager().isInSafezone(p.getLocation())) {
      p.teleport(playerFaction.getHome());

      return;
    }
    String worldName = p.getWorld().getName();
    int seconds;
    if (!this.cf.contains("Teleport-Cooldown.Home." + worldName)) {
      seconds = this.cf.getInt("Teleport-Cooldown.Home.Default", 10);
    } else {
      seconds = this.cf.getInt("Teleport-Cooldown.Home." + worldName, 10);
    }
    if (seconds == -1) {
      p.sendMessage(this.lf.getString("FACTION_HOME_CAN_NOT_TELEPORT_FROM_WORLD"));
      return;
    }

    Entry entry = null;
    if (Mango.getInstance().getGlaedr() != null) {
      entry = new Entry("stuck", PlayerScoreboard.getScoreboard(p));
      entry
          .setText(this.cf.getString("Scoreboard.Faction-Home"))
          .setCountdown(true)
          .setTime(seconds)
          .send();
    } else {
      homeTimerTicks.remove(p.getUniqueId());
      homeTimerTicks.put(p.getUniqueId(), seconds);
    }
    Warmup warmup = new Warmup(p, playerFaction.getHome(), entry);
    warmup.runTaskLater(Mango.getInstance(), seconds * 20L);
    waiting.put(p.getName(), warmup);
    p.sendMessage(ChatColor.RED + "You will be teleported to your faction home in " + seconds + " seconds!");
  }


  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player p = event.getPlayer();
    Location from = event.getFrom();
    Location to = event.getTo();

    if (!waiting.containsKey(p.getName())) {
      return;
    }

    if (to == null) {
      throw new NullPointerException("PlayerMoveEvent location \"TO\" cannot be null");
    }

    if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
      p.sendMessage(this.lf.getString("FACTION_TELEPORT_CANCELLED"));
      waiting.get(p.getName()).cancel();
      waiting.get(p.getName()).cancelEntry();
      waiting.remove(p.getName());
    }
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player p = (Player) event.getEntity();

    if (!waiting.containsKey(p.getName())) {
      return;
    }

    p.sendMessage(this.lf.getString("FACTION_TELEPORT_CANCELLED"));
    waiting.get(p.getName()).cancel();
    waiting.remove(p.getName());
  }

  public static class Warmup extends BukkitRunnable {
    private final Player player;
    private final Location location;
    @Nullable
    private final Entry entry;

    public Warmup(Player player, Location location, @Nullable Entry entry) {
      this.player = player;
      this.location = location;
      this.entry = entry;
    }

    public void cancelEntry() {
      if (entry == null) {
        homeTimerTicks.remove(this.player.getUniqueId());
        return;
      }
      this.entry.cancel();
    }

    public void run() {
      HomeCommand.waiting.remove(this.player.getName());
      this.player.teleport(this.location);
    }

    public BigDecimal getSeconds() {

      if (this.entry == null) {
        int ticks = homeTimerTicks.get(this.player.getUniqueId());
        return BigDecimal.valueOf((double) ticks / 20);
      }

      return this.entry.getTime();
    }
  }
}


