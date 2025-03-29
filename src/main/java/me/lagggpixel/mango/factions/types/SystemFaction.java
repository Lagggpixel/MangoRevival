package me.lagggpixel.mango.factions.types;

import lombok.Getter;
import lombok.Setter;
import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.utils.LocationSerialization;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


@Setter
public class SystemFaction extends Faction {

  private boolean deathban;
  @Getter
  private YamlConfiguration config = getConfiguration();
  @Getter
  private boolean deleted;
  @Getter
  private File file;

  public SystemFaction(String name) {
    super(name);
    this.deathban = true;
    this.file = new File(Mango.getInstance().getDataFolder() + File.separator + "systemfactions", getName().toLowerCase() + ".yml");
  }

  public void save() throws IOException {
    if (this.deleted) {
      return;
    }
    if (!this.file.exists()) {
      this.file.createNewFile();
    }
    this.config = YamlConfiguration.loadConfiguration(this.file);
    for (String string : this.config.getKeys(false)) {
      this.config.set(string, null);
    }
    this.config.set("name", getName());
    this.config.set("deathban", this.deathban);
    if (getHome() != null) {
      this.config.set("home", LocationSerialization.serializeLocation(getHome()));
    }
    for (Claim claim : getClaims()) {
      this.config.set("claims." + claim.getId() + ".x1", claim.getX1());
      this.config.set("claims." + claim.getId() + ".x2", claim.getX2());
      this.config.set("claims." + claim.getId() + ".z1", claim.getZ1());
      this.config.set("claims." + claim.getId() + ".z2", claim.getZ2());
      this.config.set("claims." + claim.getId() + ".world", claim.getWorld().getName());
      this.config.set("claims." + claim.getId() + ".value", claim.getValue());
    }
    this.config.save(this.file);
  }

  public void delete() {
    this.deleted = true;
    if (this.file.exists()) {
      this.file.delete();
    }
    for (Claim claim : getClaims()) {
      Mango.getInstance().getClaimManager().getClaims().remove(claim);
    }
    getClaims().clear();
    Mango.getInstance().getFactionManager().getFactions().remove(this);
  }

  public String isDeathban() {
    if (this.deathban) {
      return ChatColor.RED + "Deathban";
    }
    return ChatColor.GREEN + "Non-Deathban";
  }

  public boolean isDeathbanBoolean() {
    return this.deathban;
  }
}


