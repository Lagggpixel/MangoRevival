package me.lagggpixel.mango.classes;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Classes {
  DIAMOND(new HashMap<PotionEffectType, Integer>() {
  }),
  BARD(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.SPEED, 1);
    put(PotionEffectType.REGENERATION, 0);
    put(PotionEffectType.DAMAGE_RESISTANCE, 1);
  }}),
  ARCHER(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.SPEED, 2);
  }}),
  ROUGE(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.SPEED, 3);
    put(PotionEffectType.JUMP, 3);
  }}),
  MINER(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.FAST_DIGGING, 1);
    put(PotionEffectType.NIGHT_VISION, 0);
    put(PotionEffectType.FIRE_RESISTANCE, 0);
  }});

  private final Map<PotionEffectType, Integer> classEffects;

  Classes(Map<PotionEffectType, Integer> classEffects) {
    this.classEffects = classEffects;
  }

  public static Classes getByName(String name) {
    switch (name.toLowerCase()) {
      case "diamond": {
        return DIAMOND;
      }
      case "bard": {
        return BARD;
      }
      case "archer": {
        return ARCHER;
      }
      case "rouge": {
        return ROUGE;
      }
      case "miner": {
        return MINER;
      }
      default: {
        return null;
      }
    }
  }

  public static Classes getClassByArmourSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
    if (helmet == null || chestplate == null || leggings == null || boots == null) {
      return null;
    }
    XMaterial helmetMaterial = XMaterial.matchXMaterial(helmet.getType());
    XMaterial chestplateMaterial = XMaterial.matchXMaterial(chestplate.getType());
    XMaterial leggingsMaterial = XMaterial.matchXMaterial(leggings.getType());
    XMaterial bootsMaterial =XMaterial.matchXMaterial( boots.getType());
    if (helmetMaterial == XMaterial.LEATHER_HELMET && chestplateMaterial == XMaterial.LEATHER_CHESTPLATE && leggingsMaterial == XMaterial.LEATHER_LEGGINGS && bootsMaterial == XMaterial.LEATHER_BOOTS) {
      return Classes.ARCHER;
    } else if (helmetMaterial == XMaterial.IRON_HELMET && chestplateMaterial == XMaterial.IRON_CHESTPLATE && leggingsMaterial == XMaterial.IRON_LEGGINGS && bootsMaterial == XMaterial.IRON_BOOTS) {
      return Classes.MINER;
    } else if (helmetMaterial == XMaterial.GOLDEN_HELMET && chestplateMaterial == XMaterial.GOLDEN_CHESTPLATE && leggingsMaterial == XMaterial.GOLDEN_LEGGINGS && bootsMaterial == XMaterial.GOLDEN_BOOTS) {
      return Classes.BARD;
    } else if (helmetMaterial == XMaterial.DIAMOND_HELMET && chestplateMaterial == XMaterial.DIAMOND_CHESTPLATE && leggingsMaterial == XMaterial.DIAMOND_LEGGINGS && bootsMaterial == XMaterial.DIAMOND_BOOTS) {
      return Classes.DIAMOND;
    } else if (helmetMaterial == XMaterial.GOLDEN_HELMET && chestplateMaterial == XMaterial.CHAINMAIL_CHESTPLATE && leggingsMaterial == XMaterial.CHAINMAIL_LEGGINGS && bootsMaterial == XMaterial.GOLDEN_BOOTS) {
      return Classes.ROUGE;
    }

    return null;
  }
}