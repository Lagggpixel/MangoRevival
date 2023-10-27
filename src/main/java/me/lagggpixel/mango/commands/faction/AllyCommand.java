package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class AllyCommand extends FactionSubCommand {
  private final Mango main = Mango.getInstance();
  private final LanguageFile lf = this.main.getLanguageFile();
  private final FactionManager fm = this.main.getFactionManager();
  private final ConfigFile cf = this.main.getConfigFile();

  public AllyCommand() {
    super("ally");
  }


  public void execute(Player p, String[] args) {
    if (args.length >= 1) {
      if (this.fm.getFaction(p) == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
        return;
      }
      if (this.fm.getFaction(p) != null) {
        final PlayerFaction faction = this.fm.getFaction(p);
        if (faction.getOfficers().contains(p.getUniqueId()) || faction.isLeader(p.getUniqueId()) || p.hasPermission(this.cf.getString("ROOT_NODE") + ".ally")) {
          StringBuilder sb = new StringBuilder();
          for (String arg : args) {
            sb.append(arg).append(" ");
          }
          String name = sb.toString().trim().replace(" ", "");


          if (this.fm.getFactionByName(name) != null && this.fm.getFactionByName(name).getName().equalsIgnoreCase(faction.getName())) {
            p.sendMessage(this.lf.getString("FACTION_ALLY_SELF"));

            return;
          }
          if (faction.getAllies().size() >= this.cf.getInt("MAX_ALLIES")) {
            p.sendMessage(this.lf.getString("FACTION_TOO_MANY_ALLIES"));

            return;
          }

          if (this.fm.getFactionByName(name) == null && this.fm.getFactionByPlayerName(name) != null && this.fm.getFactionByPlayerName(name) instanceof PlayerFaction) {
            final PlayerFaction allyFaction = (PlayerFaction) this.fm.getFactionByPlayerName(name);

            if (allyFaction.getAllies().contains(faction) || faction.getAllies().contains(allyFaction)) {
              p.sendMessage(this.lf.getString("FACTION_ALREADY_RELATION").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (faction.getRequestedAllies().contains(allyFaction)) {
              p.sendMessage(this.lf.getString("FACTION_ALREADY_SENT_REQUEST").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (allyFaction.getRequestedAllies().contains(faction)) {
              allyFaction.getRequestedAllies().remove(faction);
              allyFaction.getAllies().add(faction);
              faction.getAllies().add(allyFaction);
              allyFaction.sendMessage(this.lf.getString("FACTION_ALLIED").replace("{faction}", faction.getName()));
              faction.sendMessage(this.lf.getString("FACTION_ALLIED").replace("{faction}", allyFaction.getName()));

              return;
            }

            allyFaction.sendMessage(this.lf.getString("FACTION_ALLY_RECEIVE").replace("{faction}", faction.getName()));
            faction.sendMessage(this.lf.getString("FACTION_ALLY_SEND").replace("{faction}", allyFaction.getName()));
            faction.getRequestedAllies().add(allyFaction);

            (new BukkitRunnable() {
              public void run() {
                faction.getRequestedAllies().remove(allyFaction);
              }
            }).runTaskLater(this.main, (long) (this.cf.getDouble("ALLY_REQUEST_TIMEOUT") * 20.0D));
          }


          if (this.fm.getFactionByName(name) instanceof PlayerFaction) {
            final PlayerFaction allyFaction = (PlayerFaction) this.fm.getFactionByName(name);

            if (allyFaction.getAllies().contains(faction) || faction.getAllies().contains(allyFaction)) {
              p.sendMessage(this.lf.getString("FACTION_ALREADY_RELATION").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (faction.getRequestedAllies().contains(allyFaction)) {
              p.sendMessage(this.lf.getString("FACTION_ALREADY_SENT_REQUEST").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (allyFaction.getRequestedAllies().contains(faction)) {
              allyFaction.getRequestedAllies().remove(faction);
              allyFaction.getAllies().add(faction);
              faction.getAllies().add(allyFaction);
              allyFaction.sendMessage(this.lf.getString("FACTION_ALLIED").replace("{faction}", faction.getName()));
              faction.sendMessage(this.lf.getString("FACTION_ALLIED").replace("{faction}", allyFaction.getName()));

              return;
            }

            allyFaction.sendMessage(this.lf.getString("FACTION_ALLY_RECEIVE").replace("{faction}", faction.getName()));
            faction.sendMessage(this.lf.getString("FACTION_ALLY_SEND").replace("{faction}", allyFaction.getName()));
            faction.getRequestedAllies().add(allyFaction);

            (new BukkitRunnable() {
              public void run() {
                faction.getRequestedAllies().remove(allyFaction);
              }
            }).runTaskLater(this.main, (long) (this.cf.getDouble("ALLY_REQUEST_TIMEOUT") * 20.0D));

          }

        } else {

          p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
        }

        return;
      }
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.ALLY"));
  }
}


