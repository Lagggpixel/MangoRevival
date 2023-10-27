package me.lagggpixel.mango.impl.glaedr.events;

import lombok.Getter;
import lombok.Setter;
import me.lagggpixel.mango.impl.glaedr.scoreboards.Entry;
import me.lagggpixel.mango.impl.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
/*
  This event is called when an entry is abruptly cancelled by entry#cancel
 */
public class EntryFinishEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private Entry entry;
  private PlayerScoreboard scoreboard;
  private Player player;

  public EntryFinishEvent(Entry entry, PlayerScoreboard scoreboard) {
    this.entry = entry;
    this.scoreboard = scoreboard;
    this.player = scoreboard.getPlayer();
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}