package me.lagggpixel.mango.listeners;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListeners
    implements Listener {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final Chat chat = Mango.getInstance().getChat();

  public ChatListeners() {
    Bukkit.getPluginManager().registerEvents(this, Mango.getInstance());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent e) {
    if (!e.isCancelled() &&
        this.cf.getBoolean("Chat-Prefix.Enabled")) {
      Player p = e.getPlayer();
      e.setCancelled(true);

      if (this.chat == null) {
        for (Player online : e.getRecipients()) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction == null) {
            Bukkit.broadcastMessage(this.cf.getString("Chat-Prefix.No-Faction") + p.getDisplayName() + " " + e.getMessage());
            return;
          }
          if (playerFaction.getOnlinePlayers().contains(online)) {
            online.sendMessage(this.cf.getString("Chat-Prefix.Friendly-Faction").replace("{faction}", playerFaction.getName()) + p.getDisplayName() + " " + e.getMessage());
            continue;
          }
          if (playerFaction.isAlly(online)) {
            online.sendMessage(this.cf.getString("Chat-Prefix.Ally-Faction").replace("{faction}", playerFaction.getName()) + p.getDisplayName() + " " + e.getMessage());
            continue;
          }
          online.sendMessage(this.cf.getString("Chat-Prefix.Enemy-Faction").replace("{faction}", playerFaction.getName()) + p.getDisplayName() + " " + e.getMessage());
        }
      } else {

        String name = getChatName(p);
        for (Player online : e.getRecipients()) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction == null) {
            Bukkit.broadcastMessage(this.cf.getString("Chat-Prefix.No-Faction") + e.getFormat().replace("%1$s", name).replace("%2$s", e.getMessage()).replace("<3", "❤"));
            return;
          }
          Bukkit.getConsoleSender().sendMessage(this.cf.getString("Chat-Prefix.Enemy-Faction").replace("{faction}", playerFaction.getName()) + e.getFormat().replace("%1$s", name).replace("%2$s", e.getMessage()).replace("<3", "❤"));
          if (playerFaction.getOnlinePlayers().contains(online)) {
            online.sendMessage(this.cf.getString("Chat-Prefix.Friendly-Faction").replace("{faction}", playerFaction.getName()) + e.getFormat().replace("%1$s", name).replace("%2$s", e.getMessage()).replace("<3", "❤"));
            continue;
          }
          if (playerFaction.isAlly(online)) {
            online.sendMessage(this.cf.getString("Chat-Prefix.Ally-Faction").replace("{faction}", playerFaction.getName()) + e.getFormat().replace("%1$s", name).replace("%2$s", e.getMessage()).replace("<3", "❤"));
            continue;
          }
          online.sendMessage(this.cf.getString("Chat-Prefix.Enemy-Faction").replace("{faction}", playerFaction.getName()) + e.getFormat().replace("%1$s", name).replace("%2$s", e.getMessage()).replace("<3", "❤"));
        }
      }
    }
  }


  private String getChatName(Player p) {
    return ChatColor.translateAlternateColorCodes('&', p.getDisplayName() + this.chat.getPlayerSuffix(p));
  }
}


