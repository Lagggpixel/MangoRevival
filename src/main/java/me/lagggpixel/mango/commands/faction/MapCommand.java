package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.claims.ClaimProfile;
import me.lagggpixel.mango.factions.pillars.Pillar;
import me.lagggpixel.mango.factions.pillars.PillarManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;


public class MapCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final ClaimManager cm = Mango.getInstance().getClaimManager();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final PillarManager plm = Mango.getInstance().getPillarManager();
  private final ArrayList<UUID> showing = new ArrayList<>();
  private final HashSet<ClaimProfile> profiles = new HashSet<>();

  public MapCommand() {
    super("map");
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


  public void execute(Player p, String[] args) {
    if (!this.showing.contains(p.getUniqueId())) {
      this.showing.add(p.getUniqueId());
      int totalNearby = 0;
      for (Faction faction : this.fm.getFactions()) {
        Random random = new Random();
        Material material = this.fm.getBlocks().get(random.nextInt(this.fm.getBlocks().size()));
        int messageSent = 0;
        int nearby = 0;
        for (Claim claim : faction.getClaims()) {
          Location c1 = new Location(claim.getWorld(), claim.getCornerOne().getBlockX(), p.getLocation().getBlockY(), claim.getCornerOne().getBlockZ());
          Location c2 = new Location(claim.getWorld(), claim.getCornerTwo().getBlockX(), p.getLocation().getBlockY(), claim.getCornerTwo().getBlockZ());
          Location c3 = new Location(claim.getWorld(), claim.getCornerThree().getBlockX(), p.getLocation().getBlockY(), claim.getCornerThree().getBlockZ());
          Location c4 = new Location(claim.getWorld(), claim.getCornerFour().getBlockX(), p.getLocation().getBlockY(), claim.getCornerFour().getBlockZ());
          if (claim.getWorld() == p.getWorld() && (c1.distance(p.getLocation()) < 70.0D || c2.distance(p.getLocation()) < 70.0D || c3.distance(p.getLocation()) < 70.0D || c4.distance(p.getLocation()) < 70.0D)) {
            if (messageSent == 0) {
              messageSent = 1;
            }

            c1.setY(0.0D);
            c2.setY(0.0D);
            c3.setY(0.0D);
            c4.setY(0.0D);
            (new Pillar(getProfile(p.getUniqueId()), material, (byte) 0, c1, claim.getOwner().getName() + " c1 " + p.getName())).sendPillar();
            (new Pillar(getProfile(p.getUniqueId()), material, (byte) 0, c2, claim.getOwner().getName() + " c2 " + p.getName())).sendPillar();
            (new Pillar(getProfile(p.getUniqueId()), material, (byte) 0, c3, claim.getOwner().getName() + " c3 " + p.getName())).sendPillar();
            (new Pillar(getProfile(p.getUniqueId()), material, (byte) 0, c4, claim.getOwner().getName() + " c4 " + p.getName())).sendPillar();
            continue;
          }
          nearby++;
          totalNearby++;
        }

        if (nearby < faction.getClaims().size() && !faction.getClaims().isEmpty()) {
          p.sendMessage(this.lf.getString("FACTION_MAP_DISPLAYED_FACTION").replace("{faction}", faction.getName()).replace("{block}", material.name()));
        }
      }
      if (totalNearby >= this.cm.getClaims().size()) {
        p.sendMessage(this.lf.getString("FACTION_MAP_NO_NEARBY"));
        this.showing.remove(p.getUniqueId());
      }
    } else {
      this.showing.remove(p.getUniqueId());
      p.sendMessage(this.lf.getString("FACTION_MAP_NO_DISPLAY"));
      for (Pillar pillar : this.plm.getPillars()) {
        for (Faction faction : this.fm.getFactions()) {
          if (pillar.getID().contains(faction.getName()) && pillar.getID().contains(p.getName()) && (pillar.getID().contains("c1") || pillar.getID().contains("c2") || pillar.getID().contains("c3") || pillar.getID().contains("c4")))
            pillar.removePillar();
        }
      }
    }
  }
}


