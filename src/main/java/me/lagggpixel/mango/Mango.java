package me.lagggpixel.mango;


import lombok.Getter;
import me.lagggpixel.mango.commands.FactionCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.pillars.PillarManager;
import me.lagggpixel.mango.impl.glaedr.Glaedr;
import me.lagggpixel.mango.listeners.ChatListeners;
import me.lagggpixel.mango.listeners.ClaimListeners;
import me.lagggpixel.mango.listeners.PlayerListeners;
import me.lagggpixel.mango.utils.command.BaseCommand;
import me.lagggpixel.mango.utils.command.Register;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class Mango extends JavaPlugin {
  @Getter
  private static Mango instance;
  @Getter
  boolean verified = true;
  @Getter
  private LanguageFile languageFile;
  @Getter
  private ConfigFile configFile;
  @Getter
  private FactionManager factionManager;
  @Getter
  private Economy economy;
  @Getter
  private ClaimManager claimManager;
  @Getter
  private PillarManager pillarManager;
  @Getter
  private final HashMap<UUID, Faction> claiming = new HashMap<>();
  @Getter
  private Chat chat = null;
  @Getter
  private Glaedr glaedr;
  @Getter
  private String symbol = "»";
  @Getter
  private List<Player> vanishedPlayers = new ArrayList<>();


  public void onEnable() {

    if (attemptEconomyHook()) {

      instance = this;

      this.languageFile = new LanguageFile();

      this.configFile = new ConfigFile();

      this.factionManager = new FactionManager();

      this.claimManager = new ClaimManager();

      this.pillarManager = new PillarManager();


      this.glaedr = new Glaedr(this, this.configFile.getString("SCOREBOARD_TITLE"));

      setupDirectories();


      attemptChatHook();


      registerCommands();

      registerListeners();


      this.factionManager.load();

    }

  }


  public void onDisable() {

    if (this.pillarManager != null) this.pillarManager.removeAll();

    if (this.factionManager != null && !this.factionManager.getFactions().isEmpty()) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango: Preparing to save " + this.factionManager.getFactions().size() + " factions.");

      for (Faction faction : this.factionManager.getFactions()) {

        try {

          faction.save();

        } catch (IOException e) {

          e.printStackTrace();

        }

      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango: Successfully saved " + this.factionManager.getFactions().size() + " factions.");

    }

  }


  private void setupDirectories() {

    File playerFactions = new File(getDataFolder(), "playerfactions");

    if (!playerFactions.exists()) {

      playerFactions.mkdir();

    }

    File systemFactions = new File(getDataFolder(), "systemfactions");

    if (!systemFactions.exists()) {

      systemFactions.mkdir();

    }

  }


  private void registerCommands() {

    Register register = new Register();

    try {

      register.registerCommand("faction", (BaseCommand) new FactionCommand());

    } catch (Exception e) {

      e.printStackTrace();

    }

  }


  private void registerListeners() {

    new ChatListeners();

    new ClaimListeners();

    new PlayerListeners();

  }


  private void attemptChatHook() {

    try {

      RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);

      this.chat = (Chat) rsp.getProvider();

      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Mango successfully hooked into Vault and " + rsp.getProvider().getName() + "!");

    } catch (NullPointerException e) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango failed to hook into any chat plugin, it is advised to use a chat plugin to keep rank prefixes, etc.");

    }

  }


  private boolean attemptEconomyHook() {

    if (Bukkit.getPluginManager().getPlugin("Vault") == null) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MANGO ERROR: VAULT DEPENDENCY NOT FOUND! PLUGIN DISABLING!");

      Bukkit.getPluginManager().disablePlugin((Plugin) this);

    } else {

      try {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Mango successfully hooked into Vault and " + ((Economy) economyProvider.getProvider()).getName() + "!");

        this.economy = economyProvider.getProvider();

        return true;

      } catch (NullPointerException e) {

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MANGO ERROR: ECONOMY PLUGIN NOT FOUND!");

      }

    }

    return false;

  }

}


