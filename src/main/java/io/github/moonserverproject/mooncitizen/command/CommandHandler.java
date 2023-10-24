package io.github.moonserverproject.mooncitizen.command;

import io.github.moonserverproject.mooncitizen.command.commands.MoonCitizenCommand;
import io.github.moonserverproject.mooncitizen.command.commands.NpcCommand;
import io.github.moonserverproject.mooncitizen.command.tabcompleter.MoonCitizenTabCompleter;
import io.github.moonserverproject.mooncitizen.command.tabcompleter.NpcTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CommandHandler {

  private final Plugin plugin;

  public CommandHandler(Plugin plugin) {
    this.plugin = plugin;

    addCommand("npc", new NpcCommand(), new NpcTabCompleter());
    addCommand("mooncitizen", new MoonCitizenCommand(), new MoonCitizenTabCompleter());
  }

  private void addCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
    PluginCommand command = getCommand(name);

    command.setExecutor(executor);

    if (tabCompleter != null) {
      command.setTabCompleter(tabCompleter);
    }
  }

  private PluginCommand getCommand(String command) {
    return plugin.getServer().getPluginCommand(command);
  }
}