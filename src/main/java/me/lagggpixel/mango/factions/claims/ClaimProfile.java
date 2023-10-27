package me.lagggpixel.mango.factions.claims;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimProfile {
  private UUID uuid;
  private Claim lastInside;
  private Claim inside;
  private int x1;
  private int x2;
  private int z1;
  private int z2;

  public ClaimProfile(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public Claim getLastInside() {
    return this.lastInside;
  }

  public void setLastInside(Claim lastInside) {
    this.lastInside = lastInside;
  }

  public Claim getInside() {
    return this.inside;
  }

  public void setInside(Claim inside) {
    this.inside = inside;
  }

  public int getX1() {
    return this.x1;
  }

  public void setX1(int x1) {
    this.x1 = x1;
  }

  public int getX2() {
    return this.x2;
  }

  public void setX2(int x2) {
    this.x2 = x2;
  }

  public int getZ1() {
    return this.z1;
  }

  public void setZ1(int z1) {
    this.z1 = z1;
  }

  public int getZ2() {
    return this.z2;
  }

  public void setZ2(int z2) {
    this.z2 = z2;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }
}


