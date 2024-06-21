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
    put(PotionEffectType.SPEED, 2);
    put(PotionEffectType.REGENERATION, 1);
    put(PotionEffectType.DAMAGE_RESISTANCE, 2);
  }}),
  ARCHER(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.SPEED, 3);
  }}),
  ROUGE(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.SPEED, 4);
    put(PotionEffectType.JUMP, 4);
  }}),
  MINER(new HashMap<PotionEffectType, Integer>() {{
    put(PotionEffectType.FAST_DIGGING, 2);
    put(PotionEffectType.NIGHT_VISION, 1);
    put(PotionEffectType.FIRE_RESISTANCE, 1);
  }});

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