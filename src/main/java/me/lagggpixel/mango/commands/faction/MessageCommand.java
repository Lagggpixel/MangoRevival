package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class MessageCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public MessageCommand() {
    super("message", Arrays.asList("msg", "broadcast", "bc"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction playerFaction = this.fm.getFaction(p);
    if (args.length >= 1) {
      if (playerFaction == null) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

        return;
      }
      if (!(playerFaction instanceof PlayerFaction)) {
        p.sendMessage(this.lf.getString("FACTION_NOT_IN_PLAYERFACTION"));

        return;
      }
      playerFaction.sendMessage(this.lf.getString("FACTION_CHAT_FORMAT.FACTION").replace("{player}", p.getName()).replace("{message}", StringUtils.join(args, ' ', 0, args.length)));
    } else {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.MESSAGE"));
    }
  }
}


