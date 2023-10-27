package me.lagggpixel.mango.commands;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.admin.*;
import me.lagggpixel.mango.commands.faction.*;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import me.lagggpixel.mango.utils.MessageManager;
import me.lagggpixel.mango.utils.PlayerUtility;
import me.lagggpixel.mango.utils.command.BaseCommand;
import me.lagggpixel.mango.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionCommand extends BaseCommand {
  final List<String> list = Arrays.asList("help", "create", "show", "disband", "ally", "enemy", "invite", "join", "accept", "c", "chat", "deposit", "withdraw", "version", "ver", "msg", "message", "leader", "owner", "mod", "officer", "promote", "demote", "unmod", "demod", "tag", "rename", "sethome", "broadcast", "sethome", "list", "online", "factions", "home", "h", "stuck", "uninvite", "deinvite", "claim", "unclaim", "map", "createsystem", "disbandfaction", "renamefaction", "claimfaction", "color", "setfactionhome", "deathban", "thaw", "freeze", "setdtr", "unclaimall", "save");
  private final List<FactionSubCommand> commands = new LinkedList<>();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();

  public FactionCommand() {
    super("faction", null, CommandUsageBy.PlAYER, "f");
    setMinArgs(0);
    setMaxArgs(3);

    this.commands.add(new HelpCommand());
    this.commands.add(new ShowCommand());
    this.commands.add(new DisbandCommand());
    this.commands.add(new CreateCommand());
    this.commands.add(new AllyCommand());
    this.commands.add(new EnemyCommand());
    this.commands.add(new InviteCommand());
    this.commands.add(new JoinCommand());
    this.commands.add(new LeaveCommand());
    this.commands.add(new KickCommand());
    this.commands.add(new ChatCommand());
    this.commands.add(new DepositCommand());
    this.commands.add(new WithdrawCommand());
    this.commands.add(new VersionCommand());
    this.commands.add(new MessageCommand());
    this.commands.add(new OfficerCommand());
    this.commands.add(new LeaderCommand());
    this.commands.add(new DemoteCommand());
    this.commands.add(new RenameCommand());
    this.commands.add(new ListCommand());
    this.commands.add(new SethomeCommand());
    this.commands.add(new StuckCommand());
    this.commands.add(new HomeCommand());
    this.commands.add(new UninviteCommand());
    this.commands.add(new ClaimCommand());
    this.commands.add(new UnclaimCommand());
    this.commands.add(new MapCommand());

    this.commands.add(new CreateSystemCommand());
    this.commands.add(new DisbandFactionCommand());
    this.commands.add(new RenameFactionCommand());
    this.commands.add(new ClaimFactionCommand());
    this.commands.add(new ColorCommand());
    this.commands.add(new SetFactionHomeCommand());
    this.commands.add(new DeathbanCommand());
    this.commands.add(new ThawCommand());
    this.commands.add(new FreezeCommand());
    this.commands.add(new SetDTRCommand());
    this.commands.add(new UnclaimallCommand());
    this.commands.add(new SaveCommand());
  }


  public FactionSubCommand getSubCommand(String key) {
    for (FactionSubCommand sub : this.commands) {
      if (sub.getName().equalsIgnoreCase(key)) {
        FactionSubCommand tc = sub;
        return tc;
      }
      if (sub.getAliases().contains(key.toLowerCase())) {
        FactionSubCommand tc = sub;
        return tc;
      }
    }


    return null;
  }

  public void execute(CommandSender sender, String[] args) {
    if (args.length == 0) {
      for (String msg : this.lf.getStringList("FACTION_HELP.1")) {
        sender.sendMessage(msg);
      }
    } else {
      try {
        FactionSubCommand tc = getSubCommand(args[0]);

        if (tc == null) {
          String string = this.lf.getString("FACTION_INVALID_ARGS");
          string = string.replace("{cmd}", args[0]).replace("'", "\"");
          sender.sendMessage(string);

          return;
        }
        tc.execute((Player) sender, fixArgs(args));
      } catch (Exception ex) {
        MessageManager.sendMessage(sender, "&cAn unexpected error occurred: " + ex.getLocalizedMessage() + "\nContact an admin");
        ex.printStackTrace();
      }
    }
  }

  public List<String> tabComplete(String[] args, CommandSender sender) {
    Collections.sort(this.list);

    if (sender instanceof Player) {
      Player p = (Player) sender;

      if (args.length == 0) {
        return this.list;
      }

      if (args.length == 1) {
        List<String> list1 = new ArrayList<>();
        for (String s : this.list) {
          if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
            list1.add(s);
          }
        }
        return list1;
      }

      if (args.length == 2) {
        if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("inv")) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          List<String> listToReturn = new ArrayList<>();
          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            for (Player player : PlayerUtility.getOnlinePlayers()) {
              if (!playerFaction.getPlayers().contains(player) && player.getName().toLowerCase().startsWith(args[1])) {
                listToReturn.add(player.getName());
              }
            }
          }


          return listToReturn;
        }

        if (args[0].equalsIgnoreCase("who")) {
          List<String> listToReturn = new ArrayList<>();

          for (String opt : toList(PlayerUtility.getOnlinePlayers())) {
            if (opt.toLowerCase().startsWith(args[1])) {
              listToReturn.add(opt);
            }
          }
          Collections.sort(listToReturn);
          return listToReturn;
        }

        if (args[0].equalsIgnoreCase("enemy") || args[0].equalsIgnoreCase("unally")) {
          List<String> listToReturn = new ArrayList<>();
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            for (Faction allies : playerFaction.getAllies()) {
              if (allies.getName().toLowerCase().startsWith(args[1])) {
                listToReturn.add(allies.getName().toLowerCase());
              }
            }
          }

          Collections.sort(listToReturn);
          return listToReturn;
        }

        if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("ally")) {
          List<String> listToReturn = new ArrayList<>();

          for (Faction faction : this.fm.getFactions()) {
            if (faction.getName().toLowerCase().startsWith(args[1])) {
              listToReturn.add(faction.getName());
            }
          }


          for (String opt : toList(PlayerUtility.getOnlinePlayers())) {
            if (opt.toLowerCase().startsWith(args[1]) && !listToReturn.contains(opt)) {
              listToReturn.add(opt);
            }
          }

          Collections.sort(listToReturn);
          return listToReturn;
        }

        if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("mod") || args[0].equalsIgnoreCase("officer")) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            List<String> members = new ArrayList<>();
            for (OfflinePlayer player : playerFaction.getPlayers()) {
              if (playerFaction.getMembers().contains(player.getUniqueId())) {
                members.add(player.getName());
              }
            }
            return members;
          }
        }


        if (args[0].equalsIgnoreCase("demote") || args[0].equalsIgnoreCase("demod") || args[0].equalsIgnoreCase("unmod")) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            List<String> members = new ArrayList<>();
            for (OfflinePlayer player : playerFaction.getPlayers()) {
              if (playerFaction.getOfficers().contains(player.getUniqueId())) {
                members.add(player.getName());
              }
            }
            return members;
          }
        }


        if (args[0].equalsIgnoreCase("leader") || args[0].equalsIgnoreCase("owner")) {
          PlayerFaction playerFaction = this.fm.getFaction(p);
          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            List<String> members = new ArrayList<>();
            for (OfflinePlayer player : playerFaction.getPlayers()) {
              if (playerFaction.getLeader() != player.getUniqueId()) {
                members.add(player.getName());
              }
            }
            return members;
          }
        }


        if (args[0].equalsIgnoreCase("uninvite") || args[0].equalsIgnoreCase("deinvite")) {
          PlayerFaction playerFaction = this.fm.getFaction(p);

          if (playerFaction != null && playerFaction instanceof PlayerFaction) {
            List<String> members = new ArrayList<>();
            List<OfflinePlayer> invitedPlayers = new ArrayList<>();
            for (UUID id : playerFaction.getInvitedPlayers()) {
              invitedPlayers.add(Bukkit.getOfflinePlayer(id));
            }
            for (OfflinePlayer player : invitedPlayers) {
              members.add(player.getName());
            }
            return members;
          }
        }
      }
    }


    List<String> onlinePlayers = new ArrayList<>();

    for (Player player : PlayerUtility.getOnlinePlayers()) {
      if (player.getName().toLowerCase().startsWith(args[1])) {
        onlinePlayers.add(player.getName());
      }
    }

    return onlinePlayers;
  }

  public String[] fixArgs(String[] args) {
    String[] subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, args.length - 1);
    return subArgs;
  }


  public List<String> toList(Collection<? extends Player> array) {
    List<String> list = new ArrayList<>();
    for (Player t : array) {
      list.add(t.getName());
    }
    return list;
  }
}


