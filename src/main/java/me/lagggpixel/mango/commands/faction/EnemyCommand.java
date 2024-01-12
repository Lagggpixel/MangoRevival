package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.bukkit.entity.Player;

import java.util.List;


public class EnemyCommand extends FactionSubCommand {
  private final Mango main = Mango.getInstance();
  private final LanguageFile lf = this.main.getLanguageFile();
  private final FactionManager fm = this.main.getFactionManager();
  private final ConfigFile cf = this.main.getConfigFile();

  public EnemyCommand() {
    super("enemy", List.of("unally"));
  }


  public void execute(Player p, String[] args) {
    if (args.length >= 1) {
      if (this.fm.getFaction(p) == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));
        return;
      }
      if (this.fm.getFaction(p) != null) {
        PlayerFaction faction = this.fm.getFaction(p);
        assert faction != null;
        if (faction.getOfficers().contains(p.getUniqueId()) || faction.isLeader(p.getUniqueId()) || p.hasPermission(Mango.getInstance().getRootPermissionNode() + ".enemy")) {
          StringBuilder sb = new StringBuilder();
          for (String arg : args) {
            sb.append(arg).append(" ");
          }
          String name = sb.toString().trim().replace(" ", "");

          if (this.fm.getFactionByName(name) == null) {
            p.sendMessage(this.lf.getString("FACTION_NOT_FOUND_NAME").replace("{name}", name));

            return;
          }
          if (this.fm.getFactionByName(name).getName().equalsIgnoreCase(faction.getName())) {
            p.sendMessage(this.lf.getString("FACTION_ENEMY_SELF"));


            return;
          }

          if (this.fm.getFactionByName(name) instanceof PlayerFaction allyFaction) {

            if (!allyFaction.getAllies().contains(faction) && !faction.getAllies().contains(allyFaction)) {
              p.sendMessage(this.lf.getString("FACTION_ALREADY_RELATION").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (faction.getRequestedAllies().contains(allyFaction)) {
              faction.getRequestedAllies().remove(allyFaction);
              p.sendMessage(this.lf.getString("FACTION_ALREADY_RELATION").replace("{faction}", allyFaction.getName()));

              return;
            }
            if (allyFaction.getRequestedAllies().contains(faction)) {
              allyFaction.getRequestedAllies().remove(faction);
              p.sendMessage(this.lf.getString("FACTION_ALREADY_RELATION").replace("{faction}", allyFaction.getName()));

              return;
            }

            allyFaction.sendMessage(this.lf.getString("FACTION_ENEMY").replace("{faction}", faction.getName()));
            faction.sendMessage(this.lf.getString("FACTION_ENEMY").replace("{faction}", allyFaction.getName()));
            faction.getAllies().remove(allyFaction);
            allyFaction.getAllies().remove(faction);
          }

        } else {

          p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));
        }

        return;
      }
    }
    p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.ENEMY"));
  }
}


