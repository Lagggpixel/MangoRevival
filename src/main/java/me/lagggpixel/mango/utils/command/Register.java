package me.lagggpixel.mango.utils.command;

import me.lagggpixel.mango.Mango;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


@SuppressWarnings("unused")
public class Register {
  private final SimpleCommandMap commandMap;

  public Register() {
    try {
      this.commandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer(), new Object[0]);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public BaseCommand constructFromAnnotation(final IBaseCommand base) {
    try {
      Method execute = base.getClass().getMethod("execute", CommandSender.class, String[].class);
      if (execute.isAnnotationPresent(BaseCommandAnn.class)) {
        BaseCommandAnn commandAnn = execute.getAnnotation(BaseCommandAnn.class);

        BaseCommand command = new BaseCommand(commandAnn.name(), (commandAnn.permission() == null) ? null : commandAnn.permission(), commandAnn.commandUsage(), commandAnn.aliases()) {
          public void execute(CommandSender sender, String[] args) {
            base.execute(sender, args);
          }
        };

        command.setMaxArgs(commandAnn.maxArgs());
        command.setMinArgs(commandAnn.minArgs());
        command.setUsage(commandAnn.usage());

        return command;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return null;
  }

  public void loadCommandsFromPackage(String packageName) {
    for (Class<?> clazz : getClassesInPackage(packageName)) {
      System.out.println(clazz.getName() + "\n\n");
      if (BaseCommand.class.isAssignableFrom(clazz))
        try {
          BaseCommand executor = (BaseCommand) clazz.newInstance();
          registerCommand(executor.getName(), executor);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }
  }

  private @NotNull ArrayList<Class<?>> getClassesInPackage(String pkgname) {
    JarFile jFile;
    ArrayList<Class<?>> classes = new ArrayList<>();
    CodeSource codeSource = Mango.getInstance().getClass().getProtectionDomain().getCodeSource();
    URL resource = codeSource.getLocation();
    String relPath = pkgname.replace('.', '/');
    String resPath = resource.getPath().replace("%20", " ");
    String jarPath = resPath.replaceFirst("[.]jar!.*", ".jar").replaceFirst("file:", "");

    try {
      jFile = new JarFile(jarPath);
    } catch (IOException e) {
      throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
    }
    Enumeration<JarEntry> entries = jFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String entryName = entry.getName();
      String className = null;
      if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
        className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
      }
      if (className != null) {
        Class<?> c;
        try {
          c = Class.forName(className);
        } catch (ClassNotFoundException e2) {
          throw new RuntimeException(e2);
        }
        classes.add(c);
      }
    }
    try {
      jFile.close();
    } catch (IOException e3) {
      throw new RuntimeException(e3);
    }
    return classes;
  }

  public void registerCommand(String cmd, BaseCommand executor) throws Exception {
    PluginCommand command = Bukkit.getServer().getPluginCommand(cmd.toLowerCase());

    if (command == null) {
      Constructor<?> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
      constructor.setAccessible(true);
      command = (PluginCommand) constructor.newInstance(new Object[]{cmd, Mango.getInstance()});
    }

    command.setExecutor(executor);
    List<String> list = Arrays.asList(executor.aliases);
    command.setAliases(list);

    if (command.getAliases() != null) {
      for (String alias : command.getAliases()) {
        unregisterCommand(alias);
      }
    }

    if (executor.getPermission() != null && !executor.getPermission().isEmpty()) {
      command.setPermission(executor.getPermission());
    }

    if (executor.getUsage() != null) {
      command.setUsage(executor.getUsage());
    }


    try {
      Field field = executor.getClass().getDeclaredField("description");
      field.setAccessible(true);
      if (field.get(executor) instanceof String) {
        command.setDescription(ChatColor.translateAlternateColorCodes('&', (String) field.get(executor)));
      }
    } catch (Exception ignored) {
    }

    this.commandMap.register(cmd, command);
  }

  @SuppressWarnings({"JavaReflectionMemberAccess", "unchecked"})
  public void unregisterCommand(String name) {
    try {
      Field known = SimpleCommandMap.class.getDeclaredField("knownCommands");
      Field alias = SimpleCommandMap.class.getDeclaredField("aliases");
      known.setAccessible(true);
      alias.setAccessible(true);
      Map<String, Command> knownCommands = (Map<String, Command>) known.get(this.commandMap);
      Set<String> aliases = (Set<String>) alias.get(this.commandMap);
      knownCommands.remove(name.toLowerCase());
      aliases.remove(name.toLowerCase());
    } catch (Exception ignored) {
    }
  }
}


