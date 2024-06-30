package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;


public class ChatCommand extends FactionSubCommand implements Listener {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ChatCommand() {
    super("chat", Collections.singletonList("c"));
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());
  }


  public void execute(Player p, String[] args) {
    if (args.length > 1) {
      p.sendMessage(this.lf.getString("FACTION_TOO_MANY_ARGS.CHAT"));
      return;
    }
    PlayerFaction faction = this.fm.getFaction(p);
    if (faction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
      return;
    }
    if (args.length == 1) {
      String type = args[0];
      if (type.equalsIgnoreCase("faction") || type.equalsIgnoreCase("f")) {
        faction.getAllyChat().remove(p.getUniqueId());
        faction.getFactionChat().add(p.getUniqueId());
        p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.FACTION"));
      } else if (type.equalsIgnoreCase("ally") || type.equalsIgnoreCase("a")) {
        faction.getAllyChat().add(p.getUniqueId());
        faction.getFactionChat().remove(p.getUniqueId());
        p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.ALLY"));
      } else if (type.equalsIgnoreCase("public") || type.equalsIgnoreCase("p")) {
        faction.getAllyChat().remove(p.getUniqueId());
        faction.getFactionChat().remove(p.getUniqueId());
        p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.PUBLIC"));
      } else {
        toggleChat(faction, p);
      }
      return;
    }
    toggleChat(faction, p);
  }

  private void toggleChat(PlayerFaction faction, Player p) {
    if (faction.getFactionChat().contains(p.getUniqueId())) {
      faction.getFactionChat().remove(p.getUniqueId());
      faction.getAllyChat().add(p.getUniqueId());
      p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.ALLY"));
    } else if (faction.getAllyChat().contains(p.getUniqueId())) {
      faction.getAllyChat().remove(p.getUniqueId());
      faction.getFactionChat().remove(p.getUniqueId());
      p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.PUBLIC"));
    } else {
      faction.getAllyChat().remove(p.getUniqueId());
      faction.getFactionChat().add(p.getUniqueId());
      p.sendMessage(this.lf.getString("FACTION_CHAT_CHANGED.FACTION"));
    }
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    Player p = e.getPlayer();
    if (this.fm.getFaction(p) != null && this.fm.getFaction(p) != null) {
      PlayerFaction faction = this.fm.getFaction(p);
      if (faction == null) {
        return;
      }
      if (faction.getAllyChat().contains(p.getUniqueId())) {
        e.setCancelled(true);
        faction.sendMessage(this.lf.getString("FACTION_CHAT_FORMAT.ALLY").replace("{player}", p.getName()).replace("{message}", e.getMessage()).replace("{faction}", faction.getName()));
        for (PlayerFaction allies : faction.getAllies()) {
          allies.sendMessage(this.lf.getString("FACTION_CHAT_FORMAT.ALLY").replace("{player}", p.getName()).replace("{message}", e.getMessage()).replace("{faction", faction.getName()));
        }
      } else if (faction.getFactionChat().contains(p.getUniqueId())) {
        e.setCancelled(true);
        faction.sendMessage(this.lf.getString("FACTION_CHAT_FORMAT.FACTION").replace("{player}", p.getName()).replace("{message}", e.getMessage()).replace("{faction}", faction.getName()));
      }
    }
  }
}


