package me.lagggpixel.mango.events;

import lombok.Getter;
import me.lagggpixel.mango.enums.Classes;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerClassChangeEvent extends Event {

  private final Player player;
  private final Classes oldClass;
  private final Classes newClass;
  private static final HandlerList handlers = new HandlerList();

  public PlayerClassChangeEvent(Player player, Classes oldClass, Classes newClass) {
    this.player = player;
    this.oldClass = oldClass;
    this.newClass = newClass;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
