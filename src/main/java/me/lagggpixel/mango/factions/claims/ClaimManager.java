package me.lagggpixel.mango.factions.claims;


import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.SystemFaction;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;


public class ClaimManager {
  @Getter
  private final HashSet<Claim> claims = new HashSet<>();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();


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

    return claim != null && claim.getOwner() instanceof SystemFaction &&
        !((SystemFaction) claim.getOwner()).isDeathbanBoolean();

  }


  public ItemStack getWand() {

    ItemStack stack = XMaterial.matchXMaterial(this.cf.getString("Claiming-Wand.Item")).get().parseItem();

    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(this.cf.getString("Claiming-Wand.Name"));

    meta.setLore(this.cf.getStringList("Claiming-Wand.Lore"));

    stack.setItemMeta(meta);

    return stack;

  }


  public boolean isWand(ItemStack stack) {

    return stack != null && stack.getType() == getWand().getType() && getWand().getItemMeta() != null &&
        stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equalsIgnoreCase(getWand().getItemMeta().getDisplayName()) &&
        stack.getItemMeta().getLore() != null;

  }

}


