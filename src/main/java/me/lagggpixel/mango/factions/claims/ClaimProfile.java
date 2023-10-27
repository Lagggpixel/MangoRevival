package me.lagggpixel.mango.factions.claims;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
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

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void setLastInside(Claim lastInside) {
    this.lastInside = lastInside;
  }

  public void setInside(Claim inside) {
    this.inside = inside;
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

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }
}


