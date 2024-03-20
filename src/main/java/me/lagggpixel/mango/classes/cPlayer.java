package me.lagggpixel.mango.classes;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class cPlayer {

  private final Player player;
  private Classes classes;
  private double classEnergy;
  private boolean isArcherTagged;
  private long archerTagTimer;

  public cPlayer(Player player) {
    this.player = player;
    this.classes = Classes.getClassByArmourSet(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());
    this.classEnergy = 0;
  }

  public void refreshData() {
    Classes newClasses = Classes.getClassByArmourSet(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());
    if (newClasses != this.classes) {
      this.classes = newClasses;
      this.classEnergy = 0;
    }
    if (this.classes == Classes.BARD) {
      this.classEnergy += 0.25;
    }
    if (this.classes == Classes.ROUGE) {
      this.classEnergy += 0.25;
    }
    if (this.classes == Classes.ROUGE) {
      this.classEnergy += 0.25;
    }
    if (this.isArcherTagged) {
      this.archerTagTimer -= 1;
      if (this.archerTagTimer <= 0) {
        this.isArcherTagged = false;
        this.archerTagTimer = 0;
      }
    }
  }

  public boolean hasEnergy(int i) {
    return this.classEnergy >= i;
  }

  public void removeEnergy(int i) {
    this.classEnergy -= i;
    if (this.classEnergy < 0) {
      this.classEnergy = 0;
      throw new IllegalArgumentException("Class energy cannot be negative");
    }
  }
}
