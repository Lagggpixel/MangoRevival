package me.lagggpixel.mango;


import lombok.Getter;
import me.lagggpixel.mango.commands.FactionCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.claims.ClaimManager;
import me.lagggpixel.mango.factions.pillars.PillarManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.impl.bstats.Metrics;
import me.lagggpixel.mango.impl.glaedr.Glaedr;
import me.lagggpixel.mango.listeners.ChatListeners;
import me.lagggpixel.mango.listeners.ClaimListeners;
import me.lagggpixel.mango.listeners.PlayerListeners;
import me.lagggpixel.mango.runnable.ClassesRunnable;
import me.lagggpixel.mango.utils.PlayerUtility;
import me.lagggpixel.mango.utils.command.Register;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class Mango extends JavaPlugin {
  @Getter
  private static Mango instance;
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
  private final String symbol = "»";
  @Getter
  private final List<Player> vanishedPlayers = new ArrayList<>();
  @Getter
  private boolean isPlaceholderEnabled;
  @Getter
  private boolean debug;

  private final BukkitRunnable autoSaveRunnable = new BukkitRunnable() {
    @Override
    public void run() {
      int systems = 0, players = 0;
      for (Faction faction : factionManager.getFactions()) {
        try {
          faction.save();
          if (faction instanceof PlayerFaction) {
            players++;
            continue;
          }
          systems++;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      for (Player player : PlayerUtility.getOnlinePlayers()) {
        if (player.hasPermission(Mango.getInstance().getRootPermissionNode() + ".save")) {
          player.sendMessage(languageFile.getString("SAVED.PLAYER").replace("{amount}", players + ""));
          player.sendMessage(languageFile.getString("SAVED.SYSTEM").replace("{amount}", systems + ""));
        }
      }
    }
  };
  private final ClassesRunnable classesRunnable = new ClassesRunnable();


  public void onEnable() {
    if (!attemptEconomyHook()) {
      this.getLogger().log(Level.SEVERE, "No economy hook found, disabling plugin.");
      this.onDisable();
      throw new RuntimeException("Failed to hook into economy!");
    }

    instance = this;

    new Metrics(this, 20415);

    this.languageFile = new LanguageFile();

    this.configFile = new ConfigFile();

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && configFile.getBoolean("Hooks.PlaceholderAPI")) {
      this.getLogger().log(Level.INFO, ChatColor.YELLOW + "Successfully hooked into PlaceholderAPI, enabling placeholders.");
      isPlaceholderEnabled = true;
    }

    this.factionManager = new FactionManager();

    this.claimManager = new ClaimManager();

    this.pillarManager = new PillarManager();


    this.glaedr = new Glaedr(this, this.configFile.getString("Scoreboard.Title"));
    this.glaedr.registerPlayers();

    setupDirectories();

    attemptChatHook();

    registerCommands();
    registerListeners();

    this.factionManager.load();

    autoSaveRunnable.runTaskTimerAsynchronously(this, 20L * 60 * 5, 20L * 60 * 5);
    classesRunnable.runTaskTimer(this, 20L, 20L);
  }


  public void onDisable() {

    if (this.pillarManager != null) {
      this.pillarManager.removeAll();
    }

    if (this.factionManager != null && !this.factionManager.getFactions().isEmpty()) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango: Preparing to save " + this.factionManager.getFactions().size() + " factions.");

      for (Faction faction : this.factionManager.getFactions()) {

        try {
          faction.save();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango: Successfully saved " + this.factionManager.getFactions().size() + " factions.");

    }

  }


  private void setupDirectories() {

    File playerFactions = new File(getDataFolder(), "playerfactions");

    if (!playerFactions.exists()) {

      if (!playerFactions.mkdir()) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Mango: Failed to create player factions directory.");
      }

    }

    File systemFactions = new File(getDataFolder(), "systemfactions");

    if (!systemFactions.exists()) {

      if (systemFactions.mkdir()) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Mango: Failed to create system factions directory.");
      }

    }

  }


  private void registerCommands() {

    Register register = new Register();

    try {
      register.registerCommand("faction", new FactionCommand());
    } catch (Exception e) {
      throw new RuntimeException(e);
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

      assert rsp != null;
      this.chat = rsp.getProvider();

      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Mango successfully hooked into Vault and " + rsp.getProvider().getName() + "!");

    } catch (NullPointerException e) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Mango failed to hook into any chat plugin, it is advised to use a chat plugin to keep rank prefixes, etc.");

    }

  }


  private boolean attemptEconomyHook() {

    if (Bukkit.getPluginManager().getPlugin("Vault") == null) {

      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MANGO ERROR: VAULT DEPENDENCY NOT FOUND! PLUGIN DISABLING!");

      Bukkit.getPluginManager().disablePlugin(this);

    } else {

      try {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        assert economyProvider != null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Mango successfully hooked into Vault and " + economyProvider.getProvider().getName() + "!");

        this.economy = economyProvider.getProvider();

        return true;

      } catch (NullPointerException e) {

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MANGO ERROR: ECONOMY PLUGIN NOT FOUND!");

      }

    }

    return false;

  }

  public String getRootPermissionNode() {
    return this.configFile.getString("Root-Permission-Node");
  }

}


