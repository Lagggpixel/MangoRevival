package me.lagggpixel.mango.factions.claims;


import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Getter
public class Claim {


  private LanguageFile lf = Mango.getInstance().getLanguageFile();
  private ConfigFile cf = Mango.getInstance().getConfigFile();
  private int x1;
  private int x2;
  private int z1;
  private int z2;
  private int value;
  private World world;
  private Location cornerOne;
  private FactionManager fm = Mango.getInstance().getFactionManager();
  private Location cornerTwo;
  private Location cornerThree;
  private Location cornerFour;
  private Faction owner;
  private List<UUID> players;
  private String id;


  public Claim(String id, Faction owner, int x1, int x2, int z1, int z2, World world, int value) {

    this.owner = owner;

    this.x1 = x1;

    this.x2 = x2;

    this.z1 = z1;

    this.z2 = z2;

    this.id = id;

    this.world = world;


    this.players = new ArrayList<>();


    this.cornerOne = new Location(world, x1, 82.0D, z1);

    this.cornerTwo = new Location(world, x2, 82.0D, z1);

    this.cornerThree = new Location(world, x2, 82.0D, z2);

    this.cornerFour = new Location(world, x1, 82.0D, z2);


    this.value = value;

  }


  public void setLf(LanguageFile lf) {

    this.lf = lf;
  }

  public void setCf(ConfigFile cf) {
    this.cf = cf;
  }

  public void setFm(FactionManager fm) {
    this.fm = fm;
  }


  public void setX1(int x1) {
    this.x1 = x1;
  }

  public void setX2(int x2) {
    this.x2 = x2;
  }

  public void setZ1(int z1) {
    this.z1 = z1;
  }

  public void setZ2(int z2) {
    this.z2 = z2;
  }

  public void setValue(int value) {
    this.value = value;
  }


  public void setWorld(World world) {
    this.world = world;
  }


  public void setCornerOne(Location cornerOne) {
    this.cornerOne = cornerOne;
  }

  public void setCornerTwo(Location cornerTwo) {
    this.cornerTwo = cornerTwo;
  }

  public void setCornerThree(Location cornerThree) {
    this.cornerThree = cornerThree;
  }

  public void setCornerFour(Location cornerFour) {
    this.cornerFour = cornerFour;
  }


  public void setOwner(Faction owner) {
    this.owner = owner;
  }


  public void setPlayers(List<UUID> players) {
    this.players = players;
  }

  public void setId(String id) {
    this.id = id;
  }


  public boolean overlaps(double x1, double z1, double x2, double z2) {

    double[] dim = new double[2];


    int X1 = Math.min(getX1(), getX2());

    int Z1 = Math.min(getZ1(), getZ2());

    int X2 = Math.max(getX1(), getX2());

    int Z2 = Math.max(getZ1(), getZ2());


    dim[0] = x1;

    dim[1] = x2;

    Arrays.sort(dim);

    if (X1 > dim[1] || X2 < dim[0]) {

      return false;

    }


    dim[0] = z1;

    dim[1] = z2;

    Arrays.sort(dim);

    if (Z1 > dim[1] || Z2 < dim[0]) {

      return false;

    }

    return true;

  }


  public boolean isNearby(Location l) {

    if (getWorld() == (new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).getWorld()) {

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, this.cf.getInt("Claim.Buffer")), false)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(this.cf.getInt("Claim.Buffer"), 0.0D, 0.0D), false)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, -this.cf.getInt("Claim.Buffer")), true)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-this.cf.getInt("Claim.Buffer"), 0.0D, 0.0D), true)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-this.cf.getInt("Claim.Buffer"), 0.0D, this.cf.getInt("Claim.Buffer")), false)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(this.cf.getInt("Claim.Buffer"), 0.0D, -this.cf.getInt("Claim.Buffer")), false)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-this.cf.getInt("Claim.Buffer"), 0.0D, -this.cf.getInt("Claim.Buffer")), false)) {

        return true;

      }

      if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(this.cf.getInt("Claim.Buffer"), 0.0D, this.cf.getInt("Claim.Buffer")), false)) {

        return true;

      }

    }

    return false;

  }


  public boolean isGlitched() {

    for (Claim claim : Mango.getInstance().getClaimManager().getClaims()) {

      if (claim != this) {

        if (claim.isInside(this.cornerOne, false) || claim.isInside(this.cornerTwo, false) || claim.isInside(this.cornerThree, false) || claim.isInside(this.cornerFour, false)) {

          return true;

        }

        if (isInside(claim.getCornerOne(), false) || isInside(claim.getCornerTwo(), false) || isInside(claim.getCornerThree(), false) || isInside(claim.getCornerFour(), false)) {

          return true;

        }

      }

    }

    return false;

  }


  public boolean isInside(Location loc, boolean player) {

    if (loc.getWorld() == getWorld()) {

      int x1 = Math.min(getX1(), getX2());

      int z1 = Math.min(getZ1(), getZ2());

      int x2 = Math.max(getX1(), getX2());

      int z2 = Math.max(getZ1(), getZ2());

      if (player) {

        x2++;

        z2++;

      }

      return (loc.getX() >= x1 && loc.getX() <= x2 && loc.getZ() >= z1 && loc.getZ() <= z2);

    }

    return false;

  }

}


