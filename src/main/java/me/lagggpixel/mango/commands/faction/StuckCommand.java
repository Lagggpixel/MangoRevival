package me.lagggpixel.mango.commands.faction;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.impl.glaedr.scoreboards.Entry;
import me.lagggpixel.mango.impl.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class StuckCommand extends FactionSubCommand implements Listener {
  @Getter
  private static final Map<String, Warmup> waiting = new HashMap<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  private static Map<UUID, Integer> stuckTimerTicks = new HashMap<>();

  public StuckCommand() {
    super("stuck");
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());

    if (Mango.getInstance().getGlaedr() == null) {
      BukkitRunnable homeTimerRunnable = new BukkitRunnable() {
        @Override
        public void run() {
          Map<UUID, Integer> newStuckTimerTicks = new HashMap<>();
          stuckTimerTicks.forEach((uuid, time) -> {
            if (time - 1 > 0) {
              newStuckTimerTicks.put(uuid, time - 1);
            }
          });
          stuckTimerTicks = newStuckTimerTicks;
        }
      };
      homeTimerRunnable.runTaskTimerAsynchronously(Mango.getInstance(), 0L, 1L);
    }
  }


  public void execute(Player p, String[] args) {
    if (waiting.containsKey(p.getName())) {
      p.sendMessage(this.lf.getString("FACTION_STUCK_ALREADY_WAITING"));
      return;
    }
    p.sendMessage(this.lf.getString("FACTION_TELEPORT.STUCK"));
    Random rX = new Random();
    Random rZ = new Random();
    Random rY = new Random();
    int x = rX.nextInt(20) - 10;
    int y = rY.nextInt(20) - 10;
    int z = rZ.nextInt(20) - 10;
    Location location = p.getLocation().add(x, y, z);
    int range = 0;
    while (location.getBlock().getType() == Material.AIR && location.add(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR && Mango.getInstance().getClaimManager().getClaimAt(location) == null) {
      range++;
      int newX = rX.nextInt(100) - range;
      int newY = rY.nextInt(100) - range;
      int newZ = rZ.nextInt(100) - range;
      location = p.getLocation().add(newX, newY, newZ);
    }
    location = location.getWorld().getHighestBlockAt(location).getLocation();

    int seconds;
    seconds = this.cf.getInt("Teleport-Cooldown.Stuck", 60);
    Entry entry = null;
    if (Mango.getInstance().getGlaedr() != null) {
      entry = new Entry("stuck", PlayerScoreboard.getScoreboard(p));
      entry
          .setText(this.cf.getString("Scoreboard.Faction-Home"))
          .setCountdown(true)
          .setTime(seconds)
          .send();
    } else {
      stuckTimerTicks.remove(p.getUniqueId());
      stuckTimerTicks.put(p.getUniqueId(), seconds);
    }
    waiting.put(p.getName(), new Warmup(p, location, entry));
    waiting.get(p.getName()).runTaskLater(Mango.getInstance(), (this.cf.getInt("Teleport-Cooldown.Stuck") * 20L));
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if ((e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) && waiting.containsKey(e.getPlayer().getName())) {
      waiting.get(e.getPlayer().getName()).cancel();
      waiting.get(e.getPlayer().getName()).cancelEntry();
      waiting.remove(e.getPlayer().getName());
      e.getPlayer().sendMessage(this.lf.getString("FACTION_TELEPORT_CANCELLED"));
    }
  }


  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player && waiting.containsKey(e.getEntity().getName())) {
      waiting.get(e.getEntity().getName()).cancelEntry();
      waiting.get(e.getEntity().getName()).cancel();
      waiting.remove(e.getEntity().getName());
      e.getEntity().sendMessage(this.lf.getString("FACTION_TELEPORT_CANCELLED"));
    }
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
        stuckTimerTicks.remove(this.player.getUniqueId());
        return;
      }
      this.entry.cancel();
    }

    public void run() {
      StuckCommand.waiting.remove(this.player.getName());
      this.player.teleport(this.location);
    }

    public BigDecimal getSeconds() {

      if (this.entry == null) {
        int ticks = stuckTimerTicks.get(this.player.getUniqueId());
        return BigDecimal.valueOf((double) ticks / 20);
      }

      return this.entry.getTime();
    }
  }
}


