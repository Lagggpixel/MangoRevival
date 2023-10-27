package me.lagggpixel.mango.factions.claims;


import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;


public class ClaimManager {
  private final HashSet<Claim> claims = new HashSet<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();


  public HashSet<Claim> getClaims() {

    return this.claims;

  }


  public Claim getClaimAt(Location location) {

    for (Claim claim : getClaims()) {

      if (claim.isInside(location, false)) {

        return claim;

      }

    }

    return null;

  }


  public boolean isInSafezone(Location location) {

    Claim claim = getClaimAt(location);

    if (claim != null && claim.getOwner() instanceof SystemFaction &&
        !((SystemFaction) claim.getOwner()).isDeathbanBoolean()) {

      return true;

    }


    return false;

  }


  public ItemStack getWand() {

    ItemStack stack = new ItemStack(Material.valueOf(this.cf.getString("CLAIMING_WAND.ITEM")));

    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(this.cf.getString("CLAIMING_WAND.NAME"));

    meta.setLore(this.cf.getStringList("CLAIMING_WAND.LORE"));

    stack.setItemMeta(meta);

    return stack;

  }


  public boolean isWand(ItemStack stack) {

    if (stack != null && stack.getType() == getWand().getType() &&
        stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equalsIgnoreCase(getWand().getItemMeta().getDisplayName()) &&
        stack.getItemMeta().getLore() != null) {

      return true;

    }


    return false;

  }

}


