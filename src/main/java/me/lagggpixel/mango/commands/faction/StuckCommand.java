package me.lagggpixel.mango.commands.faction;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class StuckCommand extends FactionSubCommand implements Listener {
  private static final Map<String, Warmup> waiting = new HashMap<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public StuckCommand() {
    super("stuck");
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());
  }


  public void execute(Player p, String[] args) {
    PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(p);
    if (scoreboard != null) {
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
      waiting.put(p.getName(), new Warmup(p, location, (new Entry("stuck", scoreboard)).setText(this.cf.getString("Scoreboard.Faction-Stuck")).setTime(60.0D).setCountdown(true).send()));
      waiting.get(p.getName()).runTaskLater(Mango.getInstance(), (this.cf.getInt("Teleport-Cooldown.Stuck") * 20L));
    }
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

  private static class Warmup extends BukkitRunnable {
    private final Player player;
    private final Location location;
    private final Entry entry;

    public Warmup(Player player, Location location, Entry entry) {
      this.player = player;
      this.location = location;
      this.entry = entry;
    }


    public void cancelEntry() {
      this.entry.cancel();
    }

    public void run() {
      StuckCommand.waiting.remove(this.player.getName());
      this.player.teleport(this.location);
    }
  }
}


