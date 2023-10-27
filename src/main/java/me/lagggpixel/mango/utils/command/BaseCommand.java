package me.lagggpixel.mango.utils.command;

import lombok.Getter;
import me.lagggpixel.mango.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseCommand
    implements TabExecutor {
  @Getter
  final String name;
  @Getter
  final String permission;
  @Getter
  final CommandUsageBy commandUsage;
  @Getter
  final String[] aliases;
  @Getter
  String usage;
  int maxArgs;
  int minArgs;

  public BaseCommand(String name, String permission, CommandUsageBy commandUsage, String... aliases) {
    this.name = name;
    this.commandUsage = commandUsage;
    this.permission = permission;
    this.aliases = aliases;
  }


  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (this.commandUsage.equals(CommandUsageBy.PlAYER) && !(sender instanceof org.bukkit.entity.Player)) {
      MessageManager.sendMessage(sender, "Only players can use this command.");
      return false;
    }
    if (this.commandUsage.equals(CommandUsageBy.CONSOLE)) {
      MessageManager.sendMessage(sender, "&cOnly the console can use this command.");
      return false;
    }
    if (this.permission != null && !sender.hasPermission(this.permission)) {
      MessageManager.sendMessage(sender, "&cYou don't have access to this command.");
      return false;
    }
    if (this.maxArgs >= 0 &&
        strings.length > this.maxArgs) {
      if (getUsage() != null) {
        MessageManager.sendMessage(sender, getUsage().replace("<command>", s));
      }
      return true;
    }


    if (this.minArgs >= 0 &&
        strings.length < this.minArgs) {
      if (getUsage() != null) {
        MessageManager.sendMessage(sender, getUsage().replace("<command>", s));
      }
      return true;
    }


    execute(sender, strings);


    return true;
  }


  public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
    return tabComplete(strings, sender);
  }

  public abstract void execute(CommandSender paramCommandSender, String[] paramArrayOfString);

  public List<String> tabComplete(String[] args, CommandSender sender) {
    return new ArrayList<>();
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public void setMaxArgs(int maxArgs) {
    this.maxArgs = maxArgs;
  }

  public void setMinArgs(int minArgs) {
    this.minArgs = minArgs;
  }
}


