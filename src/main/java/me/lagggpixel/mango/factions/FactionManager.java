package me.lagggpixel.mango.factions;

import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.factions.types.SystemFaction;
import me.lagggpixel.mango.utils.LocationSerialization;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


@Getter
public class FactionManager {
  private final HashSet<Faction> factions = new HashSet<>();


  public void load() throws NullPointerException {
    File systemFactions = new File(Mango.getInstance().getDataFolder(), "systemfactions");
    if (systemFactions.exists()) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Preparing to load " + (systemFactions.list()).length + " system factions.");
      for (String filePath : systemFactions.list()) {
        File file = new File(systemFactions.getPath() + File.separator + filePath);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String name = config.getString("name");
        if (!file.getName().contains(name)) {
          file.renameTo(new File(Mango.getInstance().getDataFolder() + File.separator + "systemfactions", name.toLowerCase() + ".yml"));
          try {
            config.save(file);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        boolean deathban = config.getBoolean("deathban");
        ChatColor color = ChatColor.valueOf(config.getString("color"));
        Location home = null;
        if (config.contains("home")) {
          home = LocationSerialization.deserializeLocation(config.getString("home"));
        }
        final SystemFaction faction = new SystemFaction(name);
        faction.setDeathban(deathban);
        faction.setColor(color);
        faction.setHome(home);
        if (config.contains("claims")) {
          for (String c : config.getConfigurationSection("claims").getKeys(false)) {
            int x1 = config.getInt("claims." + c + ".x1");
            int x2 = config.getInt("claims." + c + ".x2");
            int z1 = config.getInt("claims." + c + ".z1");
            int z2 = config.getInt("claims." + c + ".z2");
            int value = config.getInt("claims." + c + ".value");
            World world = Bukkit.getWorld(config.getString("claims." + c + ".world"));
            Claim claim = new Claim(c, faction, x1, x2, z1, z2, world, value);
            if (!claim.isGlitched()) {
              Mango.getInstance().getClaimManager().getClaims().add(claim);
              faction.getClaims().add(claim);
            }
          }
        }
        if (faction.getHome() != null &&
            Mango.getInstance().getClaimManager().getClaimAt(faction.getHome()).getOwner() != faction) {
          faction.setHome(null);
        }

        if (getFactionByName(name) == null) {
          this.factions.add(faction);
        }
      }
    }
    File playerFactions = new File(Mango.getInstance().getDataFolder(), "playerfactions");
    if (playerFactions.exists()) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Preparing to load " + (playerFactions.list()).length + " player factions.");
      for (String filePath : playerFactions.list()) {
        File file = new File(playerFactions.getPath() + File.separator + filePath);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String name = config.getString("name");

        if (!file.getName().contains(name)) {
          file.renameTo(new File(Mango.getInstance().getDataFolder() + File.separator + "playerfactions", name.toLowerCase() + ".yml"));
          try {
            config.save(file);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        UUID leader = UUID.fromString(config.getString("leader"));
        int balance = config.getInt("balance");
        double dtr = config.getDouble("dtr");
        int frozenInit = config.getInt("frozeninit");
        int frozenTime = config.getInt("frozentime");
        ArrayList<UUID> officers = new ArrayList<>();
        ArrayList<UUID> members = new ArrayList<>();
        ArrayList<UUID> invitedPlayers = new ArrayList<>();

        for (String ofs : config.getStringList("officers")) {
          officers.add(UUID.fromString(ofs));
        }

        for (String mems : config.getStringList("members")) {
          members.add(UUID.fromString(mems));
        }

        for (String invs : config.getStringList("invited_players")) {
          invitedPlayers.add(UUID.fromString(invs));
        }

        final List<String> allies = config.getStringList("allies");


        Location home = null;
        if (config.contains("home")) {
          home = LocationSerialization.deserializeLocation(config.getString("home"));
        }
        final PlayerFaction faction = new PlayerFaction(name, leader);
        faction.setBalance(balance);
        faction.setDtr(BigDecimal.valueOf(dtr));
        faction.setFrozenTime(frozenTime);
        faction.setFrozenInit(frozenInit);
        faction.setHome(home);
        faction.setInvitedPlayers(invitedPlayers);
        faction.setMembers(members);
        faction.setOfficers(officers);
        (new BukkitRunnable() {
          public void run() {
            for (String name : allies) {
              if (FactionManager.this.getFactionByName(name) != null && FactionManager.this.getFactionByName(name) instanceof PlayerFaction) {
                faction.getAllies().add((PlayerFaction) FactionManager.this.getFactionByName(name));
              }
            }
          }
        }).runTaskLater(Mango.getInstance(), 20L);
        if (config.contains("claims")) {
          for (String c : config.getConfigurationSection("claims").getKeys(false)) {
            int x1 = config.getInt("claims." + c + ".x1");
            int x2 = config.getInt("claims." + c + ".x2");
            int z1 = config.getInt("claims." + c + ".z1");
            int z2 = config.getInt("claims." + c + ".z2");
            int value = config.getInt("claims." + c + ".value");
            World world = Bukkit.getWorld(config.getString("claims." + c + ".world"));
            Claim claim = new Claim(c, faction, x1, x2, z1, z2, world, value);
            if (!claim.isGlitched()) {
              Mango.getInstance().getClaimManager().getClaims().add(claim);
              faction.getClaims().add(claim);
            }
          }
        }
        if (getFactionByName(name) == null && getFactionByLeader(faction.getLeader()) == null) {
          this.factions.add(faction);
        }
      }
    }
    checkDoubles();
    checkFactions();
  }

  public void checkFactions() {
    Mango.getInstance().getClaimManager().getClaims().removeIf(claim -> getFactionByName(claim.getOwner().getName()) == null);
  }

  public void checkDoubles() {
    for (Faction faction : getFactions()) {
      if (faction instanceof PlayerFaction) {
        PlayerFaction playerFaction = (PlayerFaction) faction;
        for (OfflinePlayer player : playerFaction.getPlayers()) {
          for (Faction faction1 : getFactions()) {
            if (faction1 instanceof PlayerFaction) {
              PlayerFaction playerFaction1 = (PlayerFaction) faction1;
              if (playerFaction1 != playerFaction && playerFaction1.getPlayers().contains(player)) {
                if (playerFaction1.isLeader(player.getUniqueId())) {
                  playerFaction1.delete();
                  continue;
                }
                playerFaction1.getOfficers().remove(player.getUniqueId());
                playerFaction1.getMembers().remove(player.getUniqueId());
              }
            }
          }
        }
      }
    }
  }


  public HashSet<Faction> getFaction(String name) {
    HashSet<Faction> factionSet = new HashSet<>();
    for (Faction faction : getFactions()) {

      if (faction.getName().equalsIgnoreCase(name)) {
        factionSet.add(faction);
      }

      if (faction instanceof PlayerFaction) {
        PlayerFaction playerFaction = (PlayerFaction) faction;
        for (OfflinePlayer player : playerFaction.getPlayers()) {
          if (player != null && player.getName() != null &&
              player.getName().equalsIgnoreCase(name)) {
            factionSet.add(faction);
          }
        }
      }
    }


    return factionSet;
  }

  public List<Material> getBlocks() {
    List<Material> blocks = new ArrayList<>();
    for (Material material : Material.values()) {
      if (material != Material.DIAMOND_BLOCK && !material.name().contains("SPAWNER") && !material.name().contains("STEP") && !material.name().contains("PLATE") && material.isSolid() && !material.name().contains("GLASS") && !material.name().contains("STAIRS") && !material.name().contains("FENCE") && !material.name().contains("SOIL") && !material.name().contains("BED") && !material.name().contains("DOOR") && !material.name().contains("PISTON") && !material.name().contains("DETECTOR") && !material.name().contains("FRAME") && !material.name().contains("COMMAND") && !material.name().contains("SIGN") && !material.name().contains("CAKE") && !material.name().contains("CACTUS") && !material.name().contains("HOPPER") && !material.name().contains("CHEST") && !material.name().contains("LEAVES") && !material.name().contains("EGG")) {
        blocks.add(material);
      }
    }
    return blocks;
  }

  public Faction getFactionByName(String name) {
    for (Faction faction : getFactions()) {
      if (faction.getName().equalsIgnoreCase(name)) {
        return faction;
      }
    }
    return null;
  }

  public @Nullable Faction getFactionByPlayerName(String name) {
    for (Faction faction : getFactions()) {
      if (faction instanceof PlayerFaction) {
        for (OfflinePlayer offlinePlayer : ((PlayerFaction) faction).getPlayers()) {
          if (offlinePlayer != null && offlinePlayer.getName() != null &&
              offlinePlayer.getName().equalsIgnoreCase(name)) {
            return faction;
          }
        }
      }
    }

    return null;
  }

  public PlayerFaction getFactionByLeader(UUID id) {
    for (Faction faction : getFactions()) {
      if (faction instanceof PlayerFaction && (
          (PlayerFaction) faction).isLeader(id)) {
        return (PlayerFaction) faction;
      }
    }

    return null;
  }


  public @Nullable PlayerFaction getFaction(Player p) {
    return (PlayerFaction) getFactionByPlayerName(p.getName());
  }
}


