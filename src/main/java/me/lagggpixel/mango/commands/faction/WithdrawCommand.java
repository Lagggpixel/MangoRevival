package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class WithdrawCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final Economy econ = Mango.getInstance().getEconomy();

  public WithdrawCommand() {
    super("withdraw", Arrays.asList("w"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction faction = this.fm.getFaction(p);

    if (faction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }

    if (!faction.isOfficer(p) && !faction.isLeader(p.getUniqueId())) {
      p.sendMessage(this.lf.getString("FACTION_MUST_BE_OFFICER"));

      return;
    }
    if (args.length == 0) {
      p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.WITHDRAW"));

      return;
    }
    if (!NumberUtils.isNumber(args[0])) {
      if (args[0].equalsIgnoreCase("all")) {

        if (faction.getBalance() <= 0) {
          p.sendMessage(this.lf.getString("FACTION_BROKE_ECONOMY"));

          return;
        }
        faction.sendMessage(this.lf.getString("FACTION_MONEY_WITHDRAWN").replace("{player}", p.getName()).replace("{amount}", faction.getBalance() + ""));
        this.econ.depositPlayer(p, faction.getBalance());
        faction.setBalance(0);
        return;
      }
      p.sendMessage(this.lf.getString("FACTION_INVALID_ECONOMY_AMOUNT"));

      return;
    }
    int amount = Integer.valueOf(args[0]);
    if (amount <= 0) {
      p.sendMessage(this.lf.getString("FACTION_INVALID_ECONOMY_AMOUNT"));

      return;
    }
    if (faction.getBalance() < amount) {
      p.sendMessage(this.lf.getString("FACTION_NOT_ENOUGH_BALANCE_ECONOMY"));

      return;
    }
    this.econ.depositPlayer(p, amount);
    faction.sendMessage(this.lf.getString("FACTION_MONEY_WITHDRAWN").replace("{player}", p.getName()).replace("{amount}", amount + ""));
    faction.setBalance(faction.getBalance() - amount);
  }
}


