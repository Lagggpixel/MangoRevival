 package me.lagggpixel.mango.factions;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.HashSet;

 import lombok.Getter;
 import org.bukkit.Location;
 import org.bukkit.configuration.file.YamlConfiguration;
 import me.lagggpixel.mango.factions.claims.Claim;
 
 @Getter
 public abstract class Faction
 {
   private String name;
   private File file;
   private YamlConfiguration configuration;
   private HashSet<Claim> claims;
   private Location home;
   
   public void setName(String name) {
     this.name = name; } public void setFile(File file) { this.file = file; } public void setConfiguration(YamlConfiguration configuration) { this.configuration = configuration; } public void setClaims(HashSet<Claim> claims) { this.claims = claims; } public void setHome(Location home) { this.home = home; }


   public Faction(String name) {
     this.name = name;
     this.claims = new HashSet<>();
   }


   public void delete() {}
   
   public boolean isPlayerFaction() {
     return getConfiguration().getBoolean("PlayerFaction");
   }
 
   
   public void save() throws IOException {}
 
   
   public boolean isNearBorder(Location l) {
     for (Claim claim : getClaims()) {
       if (claim.getWorld() == l.getWorld()) {
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, 1.0D), false)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 0.0D), false)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, -1.0D), true)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 0.0D), true)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 1.0D), false)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, -1.0D), false)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 1.0D), false)) {
           return true;
         }
         if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, -1.0D), false)) {
           return true;
         }
       } 
     } 
     return false;
   }
 }


