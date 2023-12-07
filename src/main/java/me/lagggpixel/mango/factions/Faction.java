package me.lagggpixel.mango.factions;

import lombok.Getter;
import me.lagggpixel.mango.factions.claims.Claim;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

@Getter
public abstract class Faction {
  private String name;
  private File file;
  private YamlConfiguration configuration;
  private HashSet<Claim> claims;
  private Location home;

  /**
   * Sets the name of the object.
   *
   * @param name the new name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the file for the function.
   *
   * @param file the file to set
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   * Sets the configuration for the object.
   *
   * @param configuration the YAML configuration to set
   */
  public void setConfiguration(YamlConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the claims for the object.
   *
   * @param claims the set of claims to be set
   */
  public void setClaims(HashSet<Claim> claims) {
    this.claims = claims;
  }

  /**
   * Sets the home location.
   *
   * @param  home the new home location
   */
  public void setHome(Location home) {
    this.home = home;
  }

  public Faction(String name) {
    this.name = name;
    this.claims = new HashSet<>();
  }

  public void delete() {
  }

  public boolean isPlayerFaction() {
    return getConfiguration().getBoolean("PlayerFaction");
  }


  public void save() throws IOException {
  }

  /**
   * Checks if the given location is near the border of a claim.
   *
   * @param  l  the location to check
   * @return    true if the location is near a claim border, false otherwise
   */
  public boolean isNearBorder(Location l) {
    for (Claim claim : getClaims()) {
      if (claim.getWorld() == l.getWorld()) {
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, 1.0D), false)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 0.0D), false)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, -1.0D), true)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 0.0D), true)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 1.0D), false)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, -1.0D), false)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 1.0D), false)) {
          return true;
        }
        if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, -1.0D), false)) {
          return true;
        }
      }
    }
    return false;
  }
}


