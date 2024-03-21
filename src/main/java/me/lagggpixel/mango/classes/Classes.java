package me.lagggpixel.mango.classes;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

@Getter
public enum Classes {
  DIAMOND(Map.of()),
  BARD(Map.of(PotionEffectType.SPEED, 2, PotionEffectType.REGENERATION, 1, PotionEffectType.DAMAGE_RESISTANCE, 2)),
  ARCHER(Map.of(PotionEffectType.SPEED, 3)),
  ROUGE(Map.of(PotionEffectType.SPEED, 4, PotionEffectType.JUMP, 4)),
  MINER(Map.of(PotionEffectType.FAST_DIGGING, 2, PotionEffectType.NIGHT_VISION, 1, PotionEffectType.FIRE_RESISTANCE, 1));

  private final Map<PotionEffectType, Integer> classEffects;

  Classes(Map<PotionEffectType, Integer> classEffects) {
    this.classEffects = classEffects;
  }

  public static Classes getByName(String name) {
    if (name.equalsIgnoreCase("DIAMOND")) {
      return DIAMOND;
    } else if (name.equalsIgnoreCase("BARD")) {
      return BARD;
    } else if (name.equalsIgnoreCase("ARCHER")) {
      return ARCHER;
    } else if (name.equalsIgnoreCase("ROUGE")) {
      return ROUGE;
    } else if (name.equalsIgnoreCase("MINER")) {
      return MINER;
    }
    return null;
  }

  public static Classes getClassByArmourSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
    if (helmet == null || chestplate == null || leggings == null || boots == null) {
      return null;
    }
    Material helmetMaterial = helmet.getType();
    Material chestplateMaterial = chestplate.getType();
    Material leggingsMaterial = leggings.getType();
    Material bootsMaterial = boots.getType();
    if (helmetMaterial == Material.LEATHER_HELMET && chestplateMaterial == Material.LEATHER_CHESTPLATE && leggingsMaterial == Material.LEATHER_LEGGINGS && bootsMaterial == Material.LEATHER_BOOTS) {
      return Classes.ARCHER;
    } else if (helmetMaterial == Material.IRON_HELMET && chestplateMaterial == Material.IRON_CHESTPLATE && leggingsMaterial == Material.IRON_LEGGINGS && bootsMaterial == Material.IRON_BOOTS) {
      return Classes.MINER;
    } else if (helmetMaterial == Material.GOLDEN_HELMET && chestplateMaterial == Material.GOLDEN_CHESTPLATE && leggingsMaterial == Material.GOLDEN_LEGGINGS && bootsMaterial == Material.GOLDEN_BOOTS) {
      return Classes.BARD;
    } else if (helmetMaterial == Material.DIAMOND_HELMET && chestplateMaterial == Material.DIAMOND_CHESTPLATE && leggingsMaterial == Material.DIAMOND_LEGGINGS && bootsMaterial == Material.DIAMOND_BOOTS) {
      return Classes.DIAMOND;
    } else if (helmetMaterial == Material.GOLDEN_HELMET && chestplateMaterial == Material.CHAINMAIL_CHESTPLATE && leggingsMaterial == Material.CHAINMAIL_LEGGINGS && bootsMaterial == Material.GOLDEN_BOOTS) {
      return Classes.ROUGE;
    }

    return null;
  }
}