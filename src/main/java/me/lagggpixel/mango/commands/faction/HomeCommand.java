package me.lagggpixel.mango.commands.faction;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class HomeCommand extends FactionSubCommand implements Listener {
  private static final Map<String, Warmup> waiting = new HashMap<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public HomeCommand() {
    super("home", Collections.singletonList("h"));
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());
  }


  public void execute(Player p, String[] args) {
    PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(p);
    if (scoreboard != null) {
      PlayerFaction playerFaction1 = this.fm.getFaction(p);

      if (playerFaction1 == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

        return;
      }
      PlayerFaction playerFaction = playerFaction1;

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
      if (p.getWorld().getName().equalsIgnoreCase("world_nether")) {
        Warmup warmup = new Warmup(p, playerFaction1.getHome(), (new Entry("stuck", scoreboard)).setText(this.cf.getString("FACTION_HOME")).setCountdown(true).setTime(25.0D).send());
        warmup.runTaskLater(Mango.getInstance(), 500L);
        waiting.put(p.getName(), warmup);
        p.sendMessage(ChatColor.RED + "You will be teleported to your faction home in 25 seconds!");
      } else if (p.getWorld().getName().equalsIgnoreCase("world")) {
        Warmup warmup = new Warmup(p, playerFaction1.getHome(), (new Entry("stuck", scoreboard)).setText(this.cf.getString("FACTION_HOME")).setCountdown(true).setTime(10.0D).send());
        warmup.runTaskLater(Mango.getInstance(), 200L);
        waiting.put(p.getName(), warmup);
        p.sendMessage(ChatColor.RED + "You will be teleported to your faction home in 10 seconds!");
      } else {
        p.sendMessage(ChatColor.RED + "You cannot teleport to your faction home in the end!");
        return;
      }
    }
  }


  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player p = event.getPlayer();
    Location from = event.getFrom();
    Location to = event.getTo();

    if (!waiting.containsKey(p.getName())) {
      return;
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
      HomeCommand.waiting.remove(this.player.getName());
      this.player.teleport(this.location);
    }
  }
}


