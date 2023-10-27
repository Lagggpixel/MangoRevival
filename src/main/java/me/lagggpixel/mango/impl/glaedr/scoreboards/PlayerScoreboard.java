package me.lagggpixel.mango.impl.glaedr.scoreboards;

import lombok.Getter;
import lombok.Setter;
import me.lagggpixel.mango.impl.glaedr.Glaedr;
import me.lagggpixel.mango.impl.glaedr.events.EntryCancelEvent;
import me.lagggpixel.mango.impl.glaedr.events.EntryFinishEvent;
import me.lagggpixel.mango.impl.glaedr.events.EntryTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
@Getter
public class PlayerScoreboard {

  @Getter
  private static final Set<PlayerScoreboard> scoreboards = new HashSet<>();

  private final Player player;
  @Setter
  private Objective objective;
  @Setter
  private Scoreboard scoreboard;
  private final Map<Entry, String> keys;
  private final Map<Entry, Integer> scores;
  private final List<Entry> entries;
  private final List<Wrapper> wrappers;
  private BukkitTask task;
  private boolean countup = false;

  public PlayerScoreboard(Glaedr main, Player player) {
    this.player = player;

    keys = new HashMap<>();
    scores = new HashMap<>();
    wrappers = new ArrayList<>();
    entries = new ArrayList<>();

    countup = main.isScoreCountUp();

    createScoreboard(main.getTitle(), main.isHook(), main.isOverrideTitle());

    for (int i = 0; i < main.getTopWrappers().size(); i++) {
      String string = main.getTopWrappers().get(i);
      new Wrapper("top_" + i, this, Wrapper.WrapperType.TOP).setText(string).send();
    }

    for (int i = 0; i < main.getBottomWrappers().size(); i++) {
      String string = main.getBottomWrappers().get(i);
      new Wrapper("bottom_" + i, this, Wrapper.WrapperType.BOTTOM).setText(string).send();
    }

    run();

    scoreboards.add(this);
  }

  public static PlayerScoreboard getScoreboard(Player player) {
    for (PlayerScoreboard playerScoreboard : getScoreboards()) {
      if (playerScoreboard.getPlayer().getName().equals(player.getName())) {
        return playerScoreboard;
      }
    }
    return null;
  }

    public String getAssignedKey(Entry entry) {
    if (keys.containsKey(entry)) {
      return keys.get(entry);
    }
    for (ChatColor color : ChatColor.values()) {

      String colorText = color + "" + ChatColor.WHITE;

      if (entry.getText().length() > 16) {
        String sub = entry.getText().substring(0, 16);
        colorText = colorText + ChatColor.getLastColors(sub);
      }

      if (!keys.containsValue(colorText)) {
        keys.put(entry, colorText);
        return colorText;
      }
    }
    throw new IndexOutOfBoundsException("No more keys available!");
  }

  public int getScore(Entry entry) {
    int start = 15 - getTopWrappers().size();
    int goal = 0;

    if (entry instanceof Wrapper) {
      Wrapper wrapper = (Wrapper) entry;
      if (wrapper.getType() == Wrapper.WrapperType.TOP) {
        goal = start;
        start = 15;
      } else {
        start = start - getEntries().size();
        goal = start - getBottomWrappers().size();
      }
    }

    for (int i = start; i > goal; i--) {
      if (!(scores.containsKey(entry))) {
        if (!(scores.containsValue(i))) {
          scores.put(entry, i);
          return i;
        }
      } else {
        int score = scores.get(entry);
        for (int toSub = 0; toSub < start; toSub++) {
          if (i - toSub > score && !scores.containsValue(i - toSub)) {
            scores.put(entry, i - toSub);
            return i - toSub;
          }
        }
        if (entry instanceof Wrapper && ((Wrapper) entry).getType() == Wrapper.WrapperType.BOTTOM) {
          if (score > start) {
            scores.put(entry, start);
            return start;
          }
        }
        return score;
      }
    }
    return 0;
  }

  public Entry getEntry(String id) {
    for (Entry entry : getEntries()) {
      if (entry.getId().equals(id)) {
        return entry;
      }
    }
    return null;
  }

  private List<Wrapper> getTopWrappers() {
    List<Wrapper> toReturn = new ArrayList<>();
    for (Wrapper wrapper : getWrappers()) {
      if (wrapper.getType() == Wrapper.WrapperType.TOP) {
        toReturn.add(wrapper);
      }
    }
    return toReturn;
  }

  private List<Wrapper> getBottomWrappers() {
    List<Wrapper> toReturn = new ArrayList<>();
    for (Wrapper wrapper : getWrappers()) {
      if (wrapper.getType() == Wrapper.WrapperType.BOTTOM) {
        toReturn.add(wrapper);
      }
    }
    return toReturn;
  }

  public void createScoreboard(String title, boolean hook, boolean overrideTitle) {
    if (hook) {
      if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
        scoreboard = player.getScoreboard();

        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
          objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
          if (overrideTitle) {
            objective.setDisplayName(title);
          }
        } else {
          objective = scoreboard.registerNewObjective(player.getName(), "dummy");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);
          objective.setDisplayName(title);
        }
        return;
      }
    }
    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    objective = scoreboard.registerNewObjective(player.getName(), "dummy");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName(title);
  }

  private void run() {
    task = new BukkitRunnable() {
      @Override
      public void run() {

        Iterator<Entry> entryIterator = getEntries().iterator();
        while (entryIterator.hasNext()) {


          Entry entry = entryIterator.next();

          if (entry.isCancelled()) {
            scoreboard.resetScores(entry.getKey());
            keys.remove(entry);
            scores.remove(entry);
            entryIterator.remove();

            if (entry.getTime() != null && entry.getTime().doubleValue() > 0) {
              Bukkit.getPluginManager().callEvent(new EntryCancelEvent(entry, PlayerScoreboard.this));
            }
            continue;
          }


          for (Wrapper wrapper : getWrappers()) {
            if (getEntries().isEmpty()) {
              scoreboard.resetScores(wrapper.getKey());
              keys.remove(wrapper);
              scores.remove(wrapper);
              continue;
            }

            wrapper.sendScoreboardUpdate(wrapper.getText());
          }

          Bukkit.getPluginManager().callEvent(new EntryTickEvent(entry, PlayerScoreboard.this));

          if (!(entry.isCountdown()) && !entry.isCountup()) {
            entry.sendScoreboardUpdate(entry.getText());
            continue;
          }

          if (entry.getTime().doubleValue() <= 0 && !entry.isCountup()) {
            entry.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new EntryFinishEvent(entry, PlayerScoreboard.this));
            continue;
          }

          if (60 > entry.getTime().intValue() || entry.isBypassAutoFormat()) {
            String toSend = entry.getText() + " " + entry.getTime();
            if (!entry.isRemoveTimeSuffix()) {
              toSend = toSend + "s";
            }
            entry.setTextTime(entry.getTime() + "s");
            entry.sendScoreboardUpdate(toSend);
            if (!(entry.isPaused())) {
              if (entry.isCountup()) {
                entry.setTime(entry.getTime().add(BigDecimal.valueOf(0.1)));
              } else {
                entry.setTime(entry.getTime().subtract(BigDecimal.valueOf(0.1)));
              }
            }
            continue;
          }
          if (3600 > entry.getTime().intValue()) {
            entry.setInterval(entry.getInterval() - 1);

            int minutes = entry.getTime().intValue() / 60;
            int seconds = entry.getTime().intValue() % 60;
            DecimalFormat formatter = new DecimalFormat("00");
            String toSend = entry.getText() + " " + formatter.format(minutes) + ":" + formatter.format(seconds);
            entry.setTextTime(formatter.format(minutes) + ":" + formatter.format(seconds));

            if (!entry.isRemoveTimeSuffix()) {
              toSend = toSend + "m";
              entry.setTextTime(formatter.format(minutes) + ":" + formatter.format(seconds) + "m");
            }

            entry.sendScoreboardUpdate(toSend);

            if (entry.getInterval() <= 0) {
              if (!(entry.isPaused())) {
                if (entry.isCountup()) {
                  entry.setTime(entry.getTime().add(BigDecimal.ONE));
                } else {
                  entry.setTime(entry.getTime().subtract(BigDecimal.ONE));
                }
              }
              entry.setInterval(10);
            }
            continue;
          }
          entry.setInterval(entry.getInterval() - 1);

          int hours = entry.getTime().intValue() / 3600;
          int minutes = (entry.getTime().intValue() % 3600) / 60;
          int seconds = entry.getTime().intValue() % 60;

          DecimalFormat formatter = new DecimalFormat("00");
          String toSend = entry.getText() + " " + formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds);
          entry.setTextTime(formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds));

          if (!entry.isRemoveTimeSuffix()) {
            toSend = toSend + "m";
            entry.setTextTime(formatter.format(minutes) + ":" + formatter.format(seconds) + "m");
          }

          entry.sendScoreboardUpdate(toSend);

          if (entry.getInterval() <= 0) {
            if (!(entry.isPaused())) {
              if (entry.isCountup()) {
                entry.setTime(entry.getTime().add(BigDecimal.ONE));
              } else {
                entry.setTime(entry.getTime().subtract(BigDecimal.ONE));
              }
            }

            entry.setInterval(10);
          }

          continue;
        }

        for (Wrapper wrapper : getWrappers()) {
          if (getEntries().isEmpty()) {
            scoreboard.resetScores(wrapper.getKey());
            keys.remove(wrapper);
            scores.remove(wrapper);
            continue;
          }

          wrapper.sendScoreboardUpdate(wrapper.getText());
        }

      }
    }.runTaskTimer(Glaedr.getPlugin(), 2L, 2L);
  }

}
