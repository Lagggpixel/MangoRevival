package me.lagggpixel.mango.factions.types;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.claims.Claim;
import me.lagggpixel.mango.utils.LocationSerialization;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public class SystemFaction extends Faction {
  private ChatColor color;
  private boolean deathban;
  private YamlConfiguration config = getConfiguration();
  private boolean deleted;

  public SystemFaction(String name) {
    super(name);
    this.color = ChatColor.WHITE;
    this.deathban = true;
    this.file = new File(Mango.getInstance().getDataFolder() + File.separator + "systemfactions", getName().toLowerCase() + ".yml");
  }

  public void setDeathban(boolean deathban) {
    this.deathban = deathban;
  }  private File file = getFile();

  public File getFile() {
    return this.file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public YamlConfiguration getConfig() {
    return this.config;
  }

  public void setConfig(YamlConfiguration config) {
    this.config = config;
  }

  public ChatColor getColor() {
    return this.color;
  }

  public void setColor(ChatColor color) {
    this.color = color;
  }

  public boolean isDeleted() {
    return this.deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
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
    this.config.set("color", getColor().name());
    this.config.set("deathban", Boolean.valueOf(this.deathban));
    if (getHome() != null) {
      this.config.set("home", LocationSerialization.serializeLocation(getHome()));
    }
    for (Claim claim : getClaims()) {
      this.config.set("claims." + claim.getId() + ".x1", Integer.valueOf(claim.getX1()));
      this.config.set("claims." + claim.getId() + ".x2", Integer.valueOf(claim.getX2()));
      this.config.set("claims." + claim.getId() + ".z1", Integer.valueOf(claim.getZ1()));
      this.config.set("claims." + claim.getId() + ".z2", Integer.valueOf(claim.getZ2()));
      this.config.set("claims." + claim.getId() + ".world", claim.getWorld().getName());
      this.config.set("claims." + claim.getId() + ".value", Integer.valueOf(claim.getValue()));
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


