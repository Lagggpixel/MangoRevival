package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.claims.ClaimProfile;
import me.lagggpixel.mango.factions.pillars.Pillar;
import me.lagggpixel.mango.factions.pillars.PillarManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


public class ClaimListeners
    implements Listener {
  private final Mango main = Mango.getInstance();
  private final LanguageFile lf = this.main.getLanguageFile();
  private final ConfigFile cf = this.main.getConfigFile();
  private final List<String> worlds = this.cf.getStringList("CLAIM_WORLDS");
  private final ClaimManager cm = this.main.getClaimManager();
  private final FactionManager fm = this.main.getFactionManager();
  private final HashSet<ClaimProfile> profiles = new HashSet<>();
  private final ArrayList<UUID> clicked = new ArrayList<>();
  private final PillarManager plm = this.main.getPillarManager();

  public ClaimListeners() {
    Bukkit.getPluginManager().registerEvents(this, (Plugin) this.main);
  }

  private ClaimProfile getProfile(UUID id) {
    for (ClaimProfile profile : this.profiles) {
      if (profile.getUuid() == id) {
        return profile;
      }
    }
    ClaimProfile newProfile = new ClaimProfile(id);
    this.profiles.add(newProfile);
    return newProfile;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onClaimInteract(PlayerInteractEvent e) {
    if (e.getAction() == Action.PHYSICAL) {
      for (Claim claim : this.cm.getClaims()) {
        if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
          PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
          if (claim.getOwner() instanceof SystemFaction) {
            if (e.getClickedBlock().getType() == Material.FARMLAND) {
              e.setCancelled(true);
            }
            return;
          }
          if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
            return;
          }
          if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
            return;
          }
          e.setCancelled(true);
        }

      }

    } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Player p = e.getPlayer();
      if (p.getItemInHand() != null && (p.getItemInHand().getType() == Material.WATER_BUCKET || p.getItemInHand().getType() == Material.LAVA_BUCKET || p.getItemInHand().getType() == Material.FLINT_AND_STEEL) && (
          e.getClickedBlock().getState() instanceof org.bukkit.inventory.InventoryHolder || e.getClickedBlock() instanceof org.bukkit.inventory.InventoryHolder || e.getClickedBlock().getType().name().contains("CHEST") || e.getClickedBlock().getType().name().contains("FURNACE") || e.getClickedBlock().getType().name().contains("GATE") || e.getClickedBlock().getType().name().contains("DOOR") || e.getClickedBlock().getType().name().contains("BUTTON") || e.getClickedBlock().getType().name().contains("LEVER"))) {
        for (Claim claim : this.cm.getClaims()) {
          if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
            PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
            if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
              return;
            }
            if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
              return;
            }
            e.setCancelled(true);
            e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
          }
        }
      }


      if (e.getClickedBlock().getState() instanceof org.bukkit.inventory.InventoryHolder || e.getClickedBlock() instanceof org.bukkit.inventory.InventoryHolder || e.getClickedBlock().getType().name().contains("CHEST") || e.getClickedBlock().getType().name().contains("FURNACE") || e.getClickedBlock().getType().name().contains("GATE") || e.getClickedBlock().getType().name().contains("DOOR") || e.getClickedBlock().getType().name().contains("BUTTON") || e.getClickedBlock().getType().name().contains("LEVER")) {
        for (Claim claim : this.cm.getClaims()) {
          if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
            PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
            if (claim.getOwner() instanceof SystemFaction) {
              return;
            }
            if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
              return;
            }
            if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
              return;
            }
            e.setCancelled(true);
            e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
          }
        }
      }
    }
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onClaimInteract(BlockBreakEvent e) {
    for (Claim claim : this.cm.getClaims()) {
      if (claim.isInside(e.getBlock().getLocation(), false)) {
        PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
        if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
          return;
        }
        if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
          return;
        }
        e.setCancelled(true);
        e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
      }
    }
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onClaimInteract(BlockPlaceEvent e) {
    for (Claim claim : this.cm.getClaims()) {
      if (claim.isInside(e.getBlock().getLocation(), false)) {
        PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
        if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
          return;
        }
        if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
          return;
        }
        e.setCancelled(true);
        e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
      }
    }
  }


  private void sendClaimChange(Player p, Faction faction, boolean entering) {
    if (entering) {
      PlayerFaction playerFaction = this.fm.getFaction(p);
      if (playerFaction == null) {
        if (faction instanceof PlayerFaction) {
          p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.ENEMY").replace("{faction}", faction.getName()));
        } else {
          SystemFaction systemFaction = (SystemFaction) faction;
          p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.SYSTEM").replace("{faction}", faction.getName()).replace("%S", systemFaction.getColor() + "").replace("{deathban}", systemFaction.isDeathban()));
        }
        return;
      }
      if (faction instanceof SystemFaction) {
        SystemFaction systemFaction = (SystemFaction) faction;
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.SYSTEM").replace("{faction}", faction.getName()).replace("%S", systemFaction.getColor() + "").replace("{deathban}", systemFaction.isDeathban()));
        return;
      }
      PlayerFaction convertedFaction = (PlayerFaction) faction;
      if (faction == playerFaction) {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.FRIENDLY").replace("{faction}", faction.getName()));
      } else if (convertedFaction.getAllies().contains(playerFaction) || playerFaction.getAllies().contains(convertedFaction)) {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.ALLY").replace("{faction}", faction.getName()));
      } else {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.ENEMY").replace("{faction}", faction.getName()));
      }
    } else {
      PlayerFaction playerFaction = this.fm.getFaction(p);
      if (playerFaction == null) {
        if (faction instanceof PlayerFaction) {
          p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.ENEMY").replace("{faction}", faction.getName()));
        } else {
          SystemFaction systemFaction = (SystemFaction) faction;
          p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.SYSTEM").replace("{faction}", faction.getName()).replace("%S", systemFaction.getColor() + "").replace("{deathban}", systemFaction.isDeathban()));
        }
        return;
      }
      if (faction instanceof SystemFaction) {
        SystemFaction systemFaction = (SystemFaction) faction;
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.SYSTEM").replace("{faction}", faction.getName()).replace("%S", systemFaction.getColor() + "").replace("{deathban}", systemFaction.isDeathban()));
        return;
      }
      PlayerFaction convertedFaction = (PlayerFaction) faction;
      if (faction == playerFaction) {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.FRIENDLY").replace("{faction}", faction.getName()));
      } else if (convertedFaction.getAllies().contains(playerFaction) || playerFaction.getAllies().contains(convertedFaction)) {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.ALLY").replace("{faction}", faction.getName()));
      } else {
        p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.ENEMY").replace("{faction}", faction.getName()));
      }
    }
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
      final Player p = e.getPlayer();
      final ClaimProfile profile = getProfile(p.getUniqueId());
      for (Claim claim : this.cm.getClaims()) {
        if (claim.isInside(e.getTo(), true) && claim.getWorld() == p.getWorld()) {
          if (profile.getLastInside() == null) {
            profile.setLastInside(claim);
            p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.SYSTEM").replace("%S{faction}", this.cf.getString("WILDERNESS.NAME")).replace("{deathban}", "Deathban"));
            sendClaimChange(p, claim.getOwner(), true);

            return;
          }
          if (profile.getLastInside().getOwner() != claim.getOwner()) {
            sendClaimChange(p, profile.getLastInside().getOwner(), false);
            sendClaimChange(p, claim.getOwner(), true);
          }

          profile.setLastInside(claim);
          continue;
        }
        if (profile.getLastInside() != null && profile.getLastInside() == claim) {
          (new BukkitRunnable() {
            public void run() {
              if (profile.getLastInside() != null && profile.getLastInside() == claim) {
                ClaimListeners.this.sendClaimChange(p, claim.getOwner(), false);
                p.sendMessage(ClaimListeners.this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.SYSTEM").replace("%S{faction}", ClaimListeners.this.cf.getString("WILDERNESS.NAME")).replace("{deathban}", "Deathban"));
                profile.setLastInside(null);
              }
            }
          }).runTaskLater((Plugin) this.main, 1L);
        }
      }
    }
  }


  @EventHandler
  public void onDrop(PlayerDropItemEvent e) {
    if (this.cm.isWand(e.getItemDrop().getItemStack())) {
      e.getItemDrop().remove();
    }
  }

  @EventHandler
  public void onStore(InventoryMoveItemEvent e) {
    if (this.cm.isWand(e.getItem())) {
      e.getSource().remove(e.getItem());
      e.getDestination().remove(e.getItem());
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (this.cm.isWand(e.getItem())) {
      Player p = e.getPlayer();
      final ClaimProfile prof = getProfile(p.getUniqueId());
      if (prof != null) {
        Faction faction = null;
        PlayerFaction playerFaction = this.fm.getFaction(p);
        if (this.main.getClaiming().containsKey(p.getUniqueId())) {
          faction = (Faction) this.main.getClaiming().get(p.getUniqueId());
        }
        if (faction != null) {
          if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            e.setCancelled(true);
            if (prof.getX1() != 0 && prof.getX2() != 0 && prof.getZ1() != 0 && prof.getZ2() != 0) {
              if (!this.clicked.contains(p.getUniqueId())) {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.CLEAR"));
                this.clicked.add(p.getUniqueId());
              } else {
                Pillar two = this.plm.getPillar(prof, "second");
                Pillar one = this.plm.getPillar(prof, "first");
                if (one != null) {
                  one.removePillar();
                }
                if (two != null) {
                  two.removePillar();
                }
                p.sendMessage(this.lf.getString("WAND_MESSAGES.CLEARED"));
                this.clicked.remove(p.getUniqueId());
                prof.setX1(0);
                prof.setZ1(0);
                prof.setZ2(0);
                prof.setX2(0);

                return;
              }
            } else {
              p.sendMessage(this.lf.getString("WAND_MESSAGES.INVALID_SELECTION"));

              return;
            }
          }
          if (e.getAction() == Action.LEFT_CLICK_AIR &&
              p.isSneaking()) {
            if (prof.getX1() != 0 && prof.getX2() != 0 && prof.getZ1() != 0 && prof.getZ2() != 0) {
              for (Claim claim1 : this.cm.getClaims()) {
                if (claim1.overlaps(prof.getX1(), prof.getZ1(), prof.getX2(), prof.getZ2()) && claim1.getWorld() == p.getWorld()) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.OVERCLAIM"));

                  return;
                }
              }
              if (!this.worlds.contains(p.getWorld().getName()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));

                return;
              }

              Pillar two = this.plm.getPillar(prof, "second");
              Pillar one = this.plm.getPillar(prof, "first");

              Location loc1 = new Location(p.getWorld(), prof.getX1(), 0.0D, prof.getZ1());
              Location loc2 = new Location(p.getWorld(), prof.getX2(), 0.0D, prof.getZ2());

              int price = (int) Math.round(loc1.distance(loc2) * this.cf.getInt("CLAIM_PRICE_MULTIPLER"));

              if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction1 = (PlayerFaction) faction;

                if (price > playerFaction1.getBalance() && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.INVALID_FUNDS"));
                  return;
                }
                playerFaction1.setBalance(playerFaction1.getBalance() - price);
                playerFaction1.sendMessage(this.lf.getString("WAND_MESSAGES.BROADCAST").replace("{player}", p.getName()));
              } else {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.BROADCAST").replace("{player}", p.getName()));
              }

              if (one != null) {
                one.removePillar();
              }
              if (two != null) {
                two.removePillar();
              }


              if (this.main.getClaiming().containsKey(p.getUniqueId())) {
                this.main.getClaiming().remove(p.getUniqueId());
              }

              Claim claim = new Claim(UUID.randomUUID().toString() + UUID.randomUUID(), faction, prof.getX1(), prof.getX2(), prof.getZ1(), prof.getZ2(), p.getWorld(), price);
              this.cm.getClaims().add(claim);
              faction.getClaims().add(claim);
              prof.setX1(0);
              prof.setZ1(0);
              prof.setZ2(0);
              prof.setX2(0);
              p.getInventory().remove(p.getItemInHand());

              return;
            }
            p.sendMessage(this.lf.getString("WAND_MESSAGES.INVALID_SELECTION"));

            return;
          }

          if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.setCancelled(true);

            if (!faction.getClaims().isEmpty() && !faction.isNearBorder(e.getClickedBlock().getLocation()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
              p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_FAR"));

              return;
            }
            for (Claim claim : this.cm.getClaims()) {
              if (claim.getWorld() == e.getClickedBlock().getLocation().getWorld()) {
                if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));
                  return;
                }
                Location blockLoc = e.getClickedBlock().getLocation();
                blockLoc.setY(claim.getCornerFour().getY());
                if (claim.isNearby(blockLoc) && claim.getOwner() != faction && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_CLOSE"));

                  return;
                }
              }
            }
            if (!this.worlds.contains(p.getWorld().getName()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
              p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));

              return;
            }
            prof.setX1(e.getClickedBlock().getX());
            prof.setZ1(e.getClickedBlock().getZ());

            if (prof.getX2() != 0 && prof.getZ2() != 0) {
              Location loc1 = new Location(p.getWorld(), prof.getX1(), 0.0D, prof.getZ1());
              Location loc2 = new Location(p.getWorld(), prof.getX2(), 0.0D, prof.getZ2());

              if (loc1.distance(loc2) < this.cf.getInt("CLAIM_MINIMUM")) {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_SMALL"));

                return;
              }
              p.sendMessage(this.lf.getString("WAND_MESSAGES.FIRST_POINT").replace("{x}", e.getClickedBlock().getX() + "").replace("{z}", e.getClickedBlock().getZ() + ""));

              int price = (int) Math.round(loc1.distance(loc2) * this.cf.getInt("CLAIM_PRICE_MULTIPLER"));
              if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction1 = (PlayerFaction) faction;


                if (playerFaction1.getBalance() < price) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_TOO_MUCH").replace("{amount}", price + ""));
                } else {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
                }
              } else {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
              }
            } else {

              p.sendMessage(this.lf.getString("WAND_MESSAGES.FIRST_POINT").replace("{x}", e.getClickedBlock().getX() + "").replace("{z}", e.getClickedBlock().getZ() + ""));
            }

            Pillar pillar = this.plm.getPillar(prof, "first");
            if (pillar != null) {
              this.plm.getPillars().remove(pillar);
              pillar.removePillar();
            }
            pillar = new Pillar(prof, Material.DIAMOND_BLOCK, (byte) 0, e.getClickedBlock().getLocation(), "first");
            pillar.sendPillar();
          }

          if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);

            for (Claim claim : this.cm.getClaims()) {
              if (claim.getWorld() == e.getClickedBlock().getLocation().getWorld()) {
                if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));
                  return;
                }
                Location blockLoc = e.getClickedBlock().getLocation();
                blockLoc.setY(claim.getCornerFour().getY());
                if (claim.isNearby(blockLoc) && claim.getOwner() != faction && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_CLOSE"));

                  return;
                }
              }
            }
            if (!this.worlds.contains(p.getWorld().getName()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
              p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));

              return;
            }

            prof.setX2(e.getClickedBlock().getX());
            prof.setZ2(e.getClickedBlock().getZ());

            if (prof.getX1() != 0 && prof.getZ1() != 0) {
              Location loc1 = new Location(p.getWorld(), prof.getX1(), 0.0D, prof.getZ1());
              Location loc2 = new Location(p.getWorld(), prof.getX2(), 0.0D, prof.getZ2());

              if (loc1.distance(loc2) < this.cf.getInt("CLAIM_MINIMUM")) {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_SMALL"));

                return;
              }
              p.sendMessage(this.lf.getString("WAND_MESSAGES.SECOND_POINT").replace("{x}", e.getClickedBlock().getX() + "").replace("{z}", e.getClickedBlock().getZ() + ""));

              int price = (int) Math.round(loc1.distance(loc2) * this.cf.getInt("CLAIM_PRICE_MULTIPLER"));

              if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction1 = (PlayerFaction) faction;

                if (playerFaction1.getBalance() < price) {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_TOO_MUCH").replace("{amount}", price + ""));
                } else {
                  p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
                }
              } else {
                p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
              }
            } else {

              p.sendMessage(this.lf.getString("WAND_MESSAGES.SECOND_POINT").replace("{x}", e.getClickedBlock().getX() + "").replace("{z}", e.getClickedBlock().getZ() + ""));
            }


            Pillar pillar = this.plm.getPillar(prof, "second");
            if (pillar != null) {
              this.plm.getPillars().remove(pillar);
              pillar.removePillar();
            }
            Pillar pillar1 = new Pillar(prof, Material.DIAMOND_BLOCK, (byte) 0, e.getClickedBlock().getLocation(), "second");
            (new BukkitRunnable() {
              public void run() {
                Pillar pillar = ClaimListeners.this.plm.getPillar(prof, "second");
                pillar.sendPillar();
              }
            }).runTaskLater((Plugin) this.main, 1L);
          }
        }
      }
    }
  }

  @EventHandler
  public void onEntitySpawn(EntitySpawnEvent e) {
    if (e.getEntity() instanceof org.bukkit.entity.Monster)
      for (Claim claim : this.cm.getClaims()) {
        if (claim.getOwner() instanceof SystemFaction) {
          SystemFaction systemFaction = (SystemFaction) claim.getOwner();
          if ((claim.isInside(e.getLocation(), true) || claim.isInside(e.getLocation(), false)) &&
              !systemFaction.isDeathbanBoolean())
            e.setCancelled(true);
        }
      }
  }
}


