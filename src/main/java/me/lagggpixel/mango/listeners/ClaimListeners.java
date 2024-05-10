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
import org.bukkit.block.Block;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


public class ClaimListeners implements Listener {
  private final Mango main = Mango.getInstance();
  private final LanguageFile lf = this.main.getLanguageFile();
  private final ConfigFile cf = this.main.getConfigFile();
  private final List<String> worlds = this.cf.getStringList("Claim.Worlds");
  private final ClaimManager cm = this.main.getClaimManager();
  private final FactionManager fm = this.main.getFactionManager();
  private final HashSet<ClaimProfile> profiles = new HashSet<>();
  private final ArrayList<UUID> clicked = new ArrayList<>();
  private final PillarManager plm = this.main.getPillarManager();

  public ClaimListeners() {
    Bukkit.getPluginManager().registerEvents(this, this.main);
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
        if (e.getClickedBlock() == null) {
          return;
        }
        if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
          PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
          if (claim.getOwner() instanceof SystemFaction) {
            if (e.getClickedBlock().getType() == Material.FARMLAND) {
              e.setCancelled(true);
            }
            return;
          }
          if (handleClaimInteraction(e, claim, playerFaction)) return;
        }

      }

    } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Player p = e.getPlayer();
      if (e.getClickedBlock() == null) {
        return;
      }
      if ((p.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET
          || p.getInventory().getItemInMainHand().getType() == Material.LAVA_BUCKET
          || p.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL)
          && (isInteractiveBlock(e.getClickedBlock()))) {
        for (Claim claim : this.cm.getClaims()) {
          if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
            PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
            if (handleClaimInteraction(e, claim, playerFaction)) return;
            e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
          }
        }
      }


      if (isInteractiveBlock(e.getClickedBlock())) {
        for (Claim claim : this.cm.getClaims()) {
          if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
            PlayerFaction playerFaction = this.fm.getFaction(e.getPlayer());
            if (claim.getOwner() instanceof SystemFaction) {
              return;
            }
            if (handleClaimInteraction(e, claim, playerFaction)) return;
            e.getPlayer().sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
          }
        }
      }
    }
  }

  /**
   * Handles the claim interaction for a player.
   *
   * @param e             the PlayerInteractEvent
   * @param claim         the Claim object
   * @param playerFaction the PlayerFaction object
   * @return true if the claim interaction is handled successfully, false otherwise
   */
  private boolean handleClaimInteraction(PlayerInteractEvent e, Claim claim, PlayerFaction playerFaction) {
    if ((playerFaction != null && playerFaction == claim.getOwner()) || e.getPlayer().hasPermission(this.cf.getString("ADMIN_NODE"))) {
      return true;
    }
    if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
      return true;
    }
    e.setCancelled(true);
    return false;
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onClaimInteract(BlockBreakEvent e) {
    handleClaimInteract(e);
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onClaimInteract(BlockPlaceEvent e) {
    handleClaimInteract(e);
  }

  /**
   * Sends a claim change notification to the player.
   *
   * @param p        the player to send the notification to
   * @param faction  the faction involved in the claim change
   * @param entering true if the player is entering the claim, false if leaving
   */
  private void sendClaimChange(Player p, Faction faction, boolean entering) {
    PlayerFaction playerFaction = this.fm.getFaction(p);
    if (entering) {
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
    if (e.getTo() == null) {
      return;
    }
    if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
      final Player p = e.getPlayer();
      final ClaimProfile profile = getProfile(p.getUniqueId());
      for (Claim claim : this.cm.getClaims()) {
        if (claim.isInside(e.getTo(), true) && claim.getWorld() == p.getWorld()) {
          if (profile.getLastInside() == null) {
            profile.setLastInside(claim);
            p.sendMessage(this.lf.getString("FACTION_CLAIM_MESSAGES.LEAVING.SYSTEM").replace("%S{faction}", this.cf.getString("Wilderness.Name")).replace("{deathban}", "Deathban"));
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
                p.sendMessage(ClaimListeners.this.lf.getString("FACTION_CLAIM_MESSAGES.ENTERING.SYSTEM").replace("%S{faction}", ClaimListeners.this.cf.getString("Wilderness.Name")).replace("{deathban}", "Deathban"));
                profile.setLastInside(null);
              }
            }
          }).runTaskLater(this.main, 1L);
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
      Faction faction = null;
      if (this.main.getClaiming().containsKey(p.getUniqueId())) {
        faction = this.main.getClaiming().get(p.getUniqueId());
      }
      if (faction == null) {
        faction = this.fm.getFaction(p);
      }
      if (faction == null) {
        return;
      }
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
      // Claim land with wand
      if (e.getAction() == Action.LEFT_CLICK_AIR && p.isSneaking()) {
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

          int price = (int) Math.round(loc1.distance(loc2) * this.cf.getInt("Claim.Price-Multiplier"));

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


          this.main.getClaiming().remove(p.getUniqueId());

          Claim claim = new Claim(UUID.randomUUID().toString() + UUID.randomUUID(), faction, prof.getX1(), prof.getX2(), prof.getZ1(), prof.getZ2(), p.getWorld(), price);
          this.cm.getClaims().add(claim);
          faction.getClaims().add(claim);
          prof.setX1(0);
          prof.setZ1(0);
          prof.setZ2(0);
          prof.setX2(0);
          p.getInventory().remove(p.getInventory().getItemInMainHand());

          return;
        }
        p.sendMessage(this.lf.getString("WAND_MESSAGES.INVALID_SELECTION"));

        return;
      }

      // Set position 1
      if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
        e.setCancelled(true);

        if (e.getClickedBlock() == null) {
          return;
        }

        if (!faction.getClaims().isEmpty() && !faction.isNearBorder(e.getClickedBlock().getLocation()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
          p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_FAR"));

          return;
        }
        if (checkIfPlayerCanClaim(e, p, faction)) {
          return;
        }
        prof.setX1(e.getClickedBlock().getX());
        prof.setZ1(e.getClickedBlock().getZ());

        handlePointSelection(p, faction, e.getClickedBlock(), prof, 1);

        Pillar pillar = this.plm.getPillar(prof, "first");
        if (pillar != null) {
          this.plm.getPillars().remove(pillar);
          pillar.removePillar();
        }
        pillar = new Pillar(prof, Material.DIAMOND_BLOCK, (byte) 0, e.getClickedBlock().getLocation(), "first");
        pillar.sendPillar();
      }

      // Set position 2
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        e.setCancelled(true);

        if (e.getClickedBlock() == null) {
          return;
        }
        if (checkIfPlayerCanClaim(e, p, faction)) {
          return;
        }
        prof.setX2(e.getClickedBlock().getX());
        prof.setZ2(e.getClickedBlock().getZ());

        handlePointSelection(p, faction, e.getClickedBlock(), prof, 2);

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
        }).runTaskLater(this.main, 1L);
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

  /**
   * Calculates the price for claiming a faction territory based on the distance between two locations.
   *
   * @param p       the player who wants to claim the territory
   * @param faction the faction that the territory belongs to
   * @param loc1    the first location
   * @param loc2    the second location
   */
  private void checkIsClaimAffordable(Player p, Faction faction, Location loc1, Location loc2) {
    int price = (int) Math.round(loc1.distance(loc2) * this.cf.getInt("Claim.Price-Multiplier"));

    if (faction instanceof PlayerFaction) {
      PlayerFaction playerFaction = (PlayerFaction) faction;
      if (playerFaction.getBalance() < price) {
        p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_TOO_MUCH").replace("{amount}", price + ""));
      } else {
        p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
      }
    } else {
      p.sendMessage(this.lf.getString("WAND_MESSAGES.COST_ENOUGH").replace("{amount}", price + ""));
    }
  }

  /**
   * Checks if a player can claim a specific block.
   *
   * @param e       the PlayerInteractEvent triggered by the player
   * @param p       the player who wants to claim the block
   * @param faction the faction the player belongs to
   * @return true if the player can claim the block, false otherwise
   */
  private boolean checkIfPlayerCanClaim(PlayerInteractEvent e, Player p, Faction faction) {
    if (e.getClickedBlock() == null) {
      return true;
    }
    for (Claim claim : this.cm.getClaims()) {
      if (claim.getWorld() == e.getClickedBlock().getLocation().getWorld()) {
        if (claim.isInside(e.getClickedBlock().getLocation(), false)) {
          p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));
          return true;
        }
        Location blockLoc = e.getClickedBlock().getLocation();
        blockLoc.setY(claim.getCornerFour().getY());
        if (claim.isNearby(blockLoc) && claim.getOwner() != faction && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
          p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_CLOSE"));

          return true;
        }
      }
    }
    if (!this.worlds.contains(p.getWorld().getName()) && !p.hasPermission(this.cf.getString("ADMIN_NODE"))) {
      p.sendMessage(this.lf.getString("WAND_MESSAGES.OTHER"));

      return true;
    }
    return false;
  }

  /**
   * Determines if a given block is an interactive block.
   *
   * @param block the block to check
   * @return true if the block is an interactive block, false otherwise
   */
  private boolean isInteractiveBlock(Block block) {
    return block.getState() instanceof org.bukkit.inventory.InventoryHolder || block instanceof org.bukkit.inventory.InventoryHolder
        || block.getType().name().contains("CHEST")
        || block.getType().name().contains("FURNACE")
        || block.getType().name().contains("GATE")
        || block.getType().name().contains("DOOR")
        || block.getType().name().contains("BUTTON")
        || block.getType().name().contains("LEVER");
  }

  private void handlePointSelection(Player p, Faction faction, Block clickedBlock, ClaimProfile prof, int claimNumber) {
    if (prof.getX2() != 0 && prof.getZ2() != 0) {
      Location loc1 = new Location(p.getWorld(), prof.getX1(), 0.0D, prof.getZ1());
      Location loc2 = new Location(p.getWorld(), prof.getX2(), 0.0D, prof.getZ2());

      if (loc1.distance(loc2) < this.cf.getInt("Claim.Minimum-Size")) {
        p.sendMessage(this.lf.getString("WAND_MESSAGES.TOO_SMALL"));
        return;
      }

      handleClaimMessage(p, clickedBlock, claimNumber);

      checkIsClaimAffordable(p, faction, loc1, loc2);
    } else {
      handleClaimMessage(p, clickedBlock, claimNumber);
    }
  }

  private void handleClaimMessage(Player p, Block clickedBlock, int claimNumber) {
    if (claimNumber == 1) {
      p.sendMessage(this.lf.getString("WAND_MESSAGES.FIRST_POINT").replace("{x}", clickedBlock.getX() + "").replace("{z}", clickedBlock.getZ() + ""));
    } else if (claimNumber == 2) {
      p.sendMessage(this.lf.getString("WAND_MESSAGES.SECOND_POINT").replace("{x}", clickedBlock.getX() + "").replace("{z}", clickedBlock.getZ() + ""));
    }
  }

  private void handleClaimInteract(BlockBreakEvent blockBreakEvent) {
    Player player = blockBreakEvent.getPlayer();
    for (Claim claim : this.cm.getClaims()) {
      if (claim.isInside(blockBreakEvent.getBlock().getLocation(), false)) {
        PlayerFaction playerFaction = this.fm.getFaction(player);
        if ((playerFaction != null && playerFaction == claim.getOwner()) || player.hasPermission(this.cf.getString("ADMIN_NODE"))) {
          return;
        }
        if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
          return;
        }
        blockBreakEvent.setCancelled(true);
        player.sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
      }
    }
  }

  private void handleClaimInteract(BlockPlaceEvent blockPlaceEvent) {
    Player player = blockPlaceEvent.getPlayer();
    for (Claim claim : this.cm.getClaims()) {
      if (claim.isInside(blockPlaceEvent.getBlock().getLocation(), false)) {
        PlayerFaction playerFaction = this.fm.getFaction(player);
        if ((playerFaction != null && playerFaction == claim.getOwner()) || player.hasPermission(this.cf.getString("ADMIN_NODE"))) {
          return;
        }
        if (claim.getOwner() instanceof PlayerFaction && ((PlayerFaction) claim.getOwner()).isRaidable()) {
          return;
        }
        blockPlaceEvent.setCancelled(true);
        player.sendMessage(this.lf.getString("FACTION_NO_INTERACT").replace("{faction}", claim.getOwner().getName()));
      }
    }
  }
}


