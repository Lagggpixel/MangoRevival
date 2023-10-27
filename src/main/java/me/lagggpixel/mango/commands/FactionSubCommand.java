package me.lagggpixel.mango.commands;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;


public abstract class FactionSubCommand {
  String name;
  boolean leader;
  boolean leaderOnly;
  List<String> aliases;

  public FactionSubCommand(String name, boolean leader, List<String> aliases, boolean leaderOnly) {
    this.name = name;
    this.leader = leader;
    this.aliases = aliases;
    this.leaderOnly = leaderOnly;
  }

  public FactionSubCommand(String name, boolean leaderOnly, List<String> aliases) {
    this(name, false, aliases, leaderOnly);
  }

  public FactionSubCommand(String name, List<String> aliases) {
    this(name, false, aliases, false);
  }

  public FactionSubCommand(String name) {
    this(name, false, new LinkedList<>(), false);
  }

  public String getName() {
    return this.name;
  }

  public List<String> getAliases() {
    return this.aliases;
  }

  public abstract void execute(Player paramPlayer, String[] paramArrayOfString);
}


