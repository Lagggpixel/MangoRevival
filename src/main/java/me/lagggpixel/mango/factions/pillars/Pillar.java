package me.lagggpixel.mango.factions.pillars;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.factions.claims.ClaimProfile;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class Pillar {
  private ClaimProfile profile;
  private Material blockType;
  private byte data;
  private ArrayList<Integer> ints;
  private Location location;
  private String ID;

  public Pillar(ClaimProfile profile, Material blockType, byte data, Location location, String ID) {
    this.profile = profile;
    this.location = location;
    this.blockType = blockType;
    this.data = data;
    this.ints = new ArrayList<>();
    this.ID = ID;
    Mango.getInstance().getPillarManager().getPillars().add(this);
  }

  public Material getBlockType() {
    return this.blockType;
  }

  public void setBlockType(Material blockType) {
    this.blockType = blockType;
  }

  public byte getData() {
    return this.data;
  }

  public void setData(byte data) {
    this.data = data;
  }

  public ArrayList<Integer> getInts() {
    return this.ints;
  }

  public void setInts(ArrayList<Integer> ints) {
    this.ints = ints;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public ClaimProfile getProfile() {
    return this.profile;
  }

  public void setProfile(ClaimProfile profile) {
    this.profile = profile;
  }

  public Pillar sendPillar() {
    int x = this.location.getBlockX();
    int z = this.location.getBlockZ();
    for (int i = 0; i <= getLocation().getWorld().getMaxHeight(); i++) {
      Location location = new Location(getLocation().getWorld(), x, i, z);
      if (location.getBlock().getType() == Material.AIR &&
          this.profile.getPlayer() != null) {
        if (this.ints.contains(Integer.valueOf(location.getBlockY()))) {
          this.profile.getPlayer().sendBlockChange(location, this.blockType, this.data);
          this.profile.getPlayer().sendBlockChange(location.add(0.0D, 2.0D, 0.0D), Material.GLASS, (byte) 0);
        } else {
          this.profile.getPlayer().sendBlockChange(location, Material.GLASS, (byte) 0);
          this.ints.add(Integer.valueOf(location.getBlockY() + 2));
        }
      }
    }

    return this;
  }


  public void removePillar() {
    int x = this.location.getBlockX();
    int z = this.location.getBlockZ();
    for (int i = 0; i <= getLocation().getWorld().getMaxHeight(); i++) {
      Location location = new Location(getLocation().getWorld(), x, i, z);
      if (location.getBlock().getType() == Material.AIR) {
        this.profile.getPlayer().sendBlockChange(location, Material.AIR, (byte) 0);
      }
    }
  }

  public void delete() {
    int x = this.location.getBlockX();
    int z = this.location.getBlockZ();
    for (int i = 0; i <= getLocation().getWorld().getMaxHeight(); i++) {
      Location location = new Location(getLocation().getWorld(), x, i, z);
      if (location.getBlock().getType() == Material.AIR)
        location.getBlock().getState().update();
    }
  }
}


