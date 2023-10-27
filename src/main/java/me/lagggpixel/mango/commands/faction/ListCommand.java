package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.Faction;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class ListCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();

  public ListCommand() {
    super("list", Arrays.asList("factions", "online"));
  }


  public void execute(Player p, String[] args) {
    if (args.length == 0) {
      for (String list : getFactionList(1)) {
        p.sendMessage(list);
      }
      return;
    }
    String stringNumber = StringUtils.join(args, ' ', 0, args.length);
    if (!NumberUtils.isNumber(stringNumber)) {
      for (String list : getFactionList(1)) {
        p.sendMessage(list);
      }
    } else {
      for (String list : getFactionList(Integer.parseInt(stringNumber))) {
        p.sendMessage(list);
      }
    }
  }

  public List<String> getFactionList(int page) {
    List<String> listToReturn = new ArrayList<>();
    listToReturn.add(this.lf.getString("FACTION_LIST_HEADER").replace("{page}", page + ""));
    final HashMap<String, Integer> factions = new HashMap<>();

    for (Faction faction : this.fm.getFactions()) {
      if (faction instanceof PlayerFaction) {
        factions.put(faction.getName(), ((PlayerFaction) faction).getOnlinePlayers().size());
      }
    }

    List<String> sorted = new ArrayList<>(factions.keySet());

    sorted.sort((s1, s2) -> ((Integer) factions.get(s2)).compareTo((Integer) factions.get(s1)));

    if (sorted.size() < page) {
      return Arrays.asList(this.lf.getString("FACTION_LIST_EMPTY"));
    }

    for (int i = page * 10 - 10; i < page * 10; i++) {
      if (sorted.size() > i) {
        PlayerFaction faction = (PlayerFaction) this.fm.getFactionByName(sorted.get(i));
        listToReturn.add(this.lf.getString("FACTION_LIST_INFO").replace("{name}", faction.getName()).replace("{position}", (i + 1) + "").replace("{online}", faction.getOnlinePlayers().size() + "").replace("{total}", faction.getPlayers().size() + "").replace("{dtr}", faction.getDtr() + "").replace("{maxdtr}", faction.getMaxDtr() + ""));
      }
    }
    listToReturn.add(this.lf.getString("FACTION_LIST_FOOTER").replace("{page}", page + ""));
    return listToReturn;
  }
}


