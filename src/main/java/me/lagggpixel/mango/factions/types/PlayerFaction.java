package me.lagggpixel.mango.factions.types;

import lombok.Getter;
import lombok.Setter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.utils.LocationSerialization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Setter
@Getter
public class PlayerFaction extends Faction {
  private Mango main = Mango.getInstance();
  private UUID leader;
  private ArrayList<UUID> officers;
  private ArrayList<UUID> members;
  private ArrayList<UUID> invitedPlayers;
  private ConfigFile configFile = this.main.getConfigFile();
  private int balance;
  private int frozenInit;
  private int frozenTime;
  private BigDecimal dtr;
  private HashSet<PlayerFaction> allies;
  private HashSet<PlayerFaction> requestedAllies;
  private YamlConfiguration config = getConfiguration();
  private List<UUID> allyChat;
  private List<UUID> factionChat;
  private boolean deleted = false;

  public PlayerFaction(String name, UUID leader) {
    super(name);
    this.file = new File(this.main.getDataFolder() + File.separator + "playerfactions", getName().toLowerCase() + ".yml");
    this.leader = leader;
    this.allyChat = new ArrayList<>();
    this.factionChat = new ArrayList<>();
    this.allies = new HashSet<>();
    this.invitedPlayers = new ArrayList<>();
    this.requestedAllies = new HashSet<>();
    this.officers = new ArrayList<>();
    this.members = new ArrayList<>();
    this.dtr = BigDecimal.valueOf(this.configFile.getDouble("Faction.Dtr-Per-Player"));
    this.balance = this.configFile.getInt("Faction.Starting-Balance");
    checkRegen();
    checkDTR();
  }

  private File file;

  public void save() throws IOException {
    if (this.deleted) {
      return;
    }

    this.file = new File(this.main.getDataFolder() + File.separator + "playerfactions", getName().toLowerCase() + ".yml");

    if (!this.file.exists()) {
      this.file.createNewFile();
    }

    ArrayList<String> mems = new ArrayList<>();
    ArrayList<String> ofs = new ArrayList<>();
    ArrayList<String> invs = new ArrayList<>();
    ArrayList<String> als = new ArrayList<>();

    for (UUID memsid : getMembers()) {
      mems.add(memsid.toString());
    }
    for (UUID ofsid : getOfficers()) {
      ofs.add(ofsid.toString());
    }
    for (PlayerFaction faction : getAllies()) {
      als.add(faction.getName());
    }
    for (UUID uuid : this.invitedPlayers) {
      invs.add(uuid.toString());
    }

    this.config = YamlConfiguration.loadConfiguration(this.file);
    for (String string : this.config.getKeys(false)) {
      this.config.set(string, null);
    }
    this.config.save(this.file);
    this.config.set("name", getName());
    this.config.set("leader", this.leader.toString());
    this.config.set("officers", ofs);
    this.config.set("members", mems);
    this.config.set("allies", als);
    this.config.set("invited_players", invs);
    this.config.set("dtr", this.dtr.doubleValue());
    this.config.set("balance", this.balance);
    this.config.set("frozeninit", this.frozenInit);
    this.config.set("frozentime", this.frozenTime);
    for (Claim claim : getClaims()) {
      this.config.set("claims." + claim.getId() + ".x1", claim.getX1());
      this.config.set("claims." + claim.getId() + ".x2", claim.getX2());
      this.config.set("claims." + claim.getId() + ".z1", claim.getZ1());
      this.config.set("claims." + claim.getId() + ".z2", claim.getZ2());
      this.config.set("claims." + claim.getId() + ".world", claim.getWorld().getName());
      this.config.set("claims." + claim.getId() + ".value", claim.getValue());
    }
    if (getHome() != null) {
      this.config.set("home", LocationSerialization.serializeLocation(getHome()));
    }
    this.config.save(this.file);
  }

  public ArrayList<Player> getOnlinePlayers() {
    ArrayList<Player> onlinePlayers = new ArrayList<>();

    for (UUID id : getIDs()) {
      Player player = Bukkit.getPlayer(id);
      if (player != null) {
        onlinePlayers.add(player);
      }
    }

    return onlinePlayers;
  }

  public ArrayList<OfflinePlayer> getPlayers() {
    ArrayList<OfflinePlayer> offlinePlayers = new ArrayList<>();
    for (UUID ids : getIDs()) {
      offlinePlayers.add(Bukkit.getOfflinePlayer(ids));
    }

    return offlinePlayers;
  }

  public boolean isAlly(Player p) {
    for (PlayerFaction faction : getAllies()) {
      if (faction.getPlayers().contains(p)) {
        return true;
      }
    }
    return false;
  }

  public OfflinePlayer getPlayer(String name) {
    for (OfflinePlayer offlinePlayer : getPlayers()) {
      if (offlinePlayer.getName().equalsIgnoreCase(name)) {
        return offlinePlayer;
      }
    }
    return null;
  }

  public ArrayList<UUID> getIDs() {
    ArrayList<UUID> ids = new ArrayList<>();
    ids.add(this.leader);
    ids.addAll(getOfficers());
    ids.addAll(getMembers());

    return ids;
  }

  public boolean isLeader(UUID id) {
    return this.leader.toString().equals(id.toString());
  }

  public void delete() {
    Mango.getInstance().getFactionManager().getFactions().remove(this);
    this.deleted = true;
    if (this.file.exists()) {
      this.file.delete();
    }
    for (Claim claim : getClaims()) {
      Mango.getInstance().getClaimManager().getClaims().remove(claim);
    }
    for (PlayerFaction ally : getAllies()) {
      ally.getAllies().remove(this);
    }
    getClaims().clear();
  }

  public void sendMessage(String message) {
    for (Player online : getOnlinePlayers()) {
      online.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
  }

  public void freeze(int duration) {
    setFrozenInit((int) (System.currentTimeMillis() / 1000L));
    setFrozenTime(duration);
  }

  public double getMaxDtr() {
    BigDecimal currentDtr = BigDecimal.valueOf(0L);
    double perPlayer = this.configFile.getDouble("Faction.Dtr-Per-Player");
    for (int i = 0; i < getPlayers().size(); i++) {
      currentDtr = currentDtr.add(BigDecimal.valueOf(perPlayer));
    }
    return Math.min(currentDtr.doubleValue(), this.configFile.getDouble("Faction.Max-Dtr"));
  }

  public int[] getFreezeLength() {
    long time = (getFrozenTime() + getFrozenInit()) - System.currentTimeMillis() / 1000L;
    int hours = 0;


    int time2 = (int) time;
    if (time / 3600L > 0L)
      hours = (int) (time / 3600L);
    int l;
    for (l = 0; l < hours; l++) {
      time2 -= 3600;
    }
    int minutes = time2 / 60;
    for (l = 0; l < minutes; l++) {
      time2 -= 60;
    }
    int seconds = time2;
    int[] times = new int[3];
    times[0] = hours;
    times[1] = minutes;
    times[2] = seconds;
    return times;
  }

  public String getFreezeTime() {
    if (getFrozenTime() == -1) {
      return "Never";
    }
    int[] time = getFreezeLength();
    if (time[0] == 0 && time[1] == 0 && time[2] == 0)
      return "Now";
    if (time[0] > 0 && time[1] > 0 && time[2] > 0)
      return time[0] + " hours, " + time[1] + " minutes and " + time[2] + " seconds";
    if (time[0] == 0 && time[1] > 0 && time[2] > 0)
      return time[1] + " minutes and " + time[2] + " seconds";
    if (time[0] == 0 && time[1] == 0 && time[2] > 0)
      return time[2] + " seconds";
    if (time[0] > 0 && time[1] == 0 && time[2] == 0)
      return time[0] + " hours";
    if (time[0] > 0 && time[1] > 0 && time[2] == 0)
      return time[0] + " hours and " + time[1] + " minutes";
    if (time[0] == 0 && time[1] > 0 && time[2] == 0) {
      return time[1] + " minutes";
    }
    return "Now";
  }

  public boolean isRaidable() {
    return (this.dtr.doubleValue() <= 0.0D);
  }

  public boolean isFrozen() {
    return (this.frozenTime > 0 && this.frozenInit > 0);
  }

  public double getDtr() {
    return this.dtr.doubleValue();
  }

  public BigDecimal getDtrDecimal() {
    return this.dtr;
  }

  private void checkDTR() {
    (new BukkitRunnable() {
      public void run() {
        if (PlayerFaction.this.getDtrDecimal().doubleValue() > PlayerFaction.this.getMaxDtr()) {
          PlayerFaction.this.setDtr(BigDecimal.valueOf(PlayerFaction.this.getMaxDtr()));
        }
        if (PlayerFaction.this.isFrozen()) {
          int[] freezeTime = PlayerFaction.this.getFreezeLength();
          if (freezeTime[0] <= 0 && freezeTime[1] <= 0 && freezeTime[2] <= 0) {
            PlayerFaction.this.unfreeze();
          }
        }
      }
    }).runTaskTimerAsynchronously(this.main, 20L, 20L);
  }

  public void unfreeze() {
    this.frozenInit = 0;
    this.frozenTime = 0;
  }

  private void checkRegen() {
    (new BukkitRunnable() {
      public void run() {
        if (!PlayerFaction.this.isFrozen() &&
            PlayerFaction.this.getDtrDecimal().doubleValue() < PlayerFaction.this.getMaxDtr()) {
          PlayerFaction.this.setDtr(PlayerFaction.this.getDtrDecimal().add(BigDecimal.valueOf(0.1D)));

        }
      }
    }).runTaskTimerAsynchronously(this.main, Mango.getInstance().getConfigFile().getInt("Faction.Dtr-Regen-Speed"), (20L * Mango.getInstance().getConfigFile().getInt("Faction.Dtr-Regen-Speed")));
  }

  public boolean isOfficer(Player player) {
    return getOfficers().contains(player.getUniqueId());
  }


}


