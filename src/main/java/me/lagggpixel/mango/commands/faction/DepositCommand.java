package me.lagggpixel.mango.commands.faction;

import me.lagggpixel.mango.Mango;
import me.lagggpixel.mango.commands.FactionSubCommand;
import me.lagggpixel.mango.config.ConfigFile;
import me.lagggpixel.mango.config.LanguageFile;
import me.lagggpixel.mango.factions.FactionManager;
import me.lagggpixel.mango.factions.types.PlayerFaction;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class DepositCommand extends FactionSubCommand {
  private final LanguageFile lf = Mango.getInstance().getLanguageFile();
  private final ConfigFile cf = Mango.getInstance().getConfigFile();
  private final FactionManager fm = Mango.getInstance().getFactionManager();
  private final Economy econ = Mango.getInstance().getEconomy();

  public DepositCommand() {
    super("deposit", Arrays.asList("d"));
  }


  public void execute(Player p, String[] args) {
    PlayerFaction faction = this.fm.getFaction(p);

    if (faction == null) {
      p.sendMessage(this.lf.getString("FACTION_NOT_IN"));

      return;
    }
    if (faction instanceof PlayerFaction) {

      if (args.length == 0) {
        p.sendMessage(this.lf.getString("FACTION_TOO_FEW_ARGS.DEPOSIT"));

        return;
      }
      if (!NumberUtils.isNumber(args[0])) {
        if (args[0].equalsIgnoreCase("all")) {

          int balance = (int) this.econ.getBalance(p);

          if (balance <= 0) {
            p.sendMessage(this.lf.getString("FACTION_INVALID_ECONOMY_AMOUNT"));

            return;
          }
          faction.setBalance(faction.getBalance() + balance);
          faction.sendMessage(this.lf.getString("FACTION_MONEY_DEPOSITED").replace("{player}", p.getName()).replace("{amount}", balance + ""));
          this.econ.withdrawPlayer(p, this.econ.getBalance(p));
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
      if (this.econ.getBalance(p) < amount) {
        p.sendMessage(this.lf.getString("FACTION_NOT_ENOUGH_ECONOMY"));

        return;
      }
      this.econ.withdrawPlayer(p, amount);
      faction.setBalance(faction.getBalance() + amount);
      faction.sendMessage(this.lf.getString("FACTION_MONEY_DEPOSITED").replace("{player}", p.getName()).replace("{amount}", amount + ""));
    }
  }
}


