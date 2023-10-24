package io.github.moonserverproject.mooncitizen.command.commands;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundleString;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getNpcConfig;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MoonCitizenCommand implements CommandExecutor {

  /**
   * Executes the given command, returning its success.
   * <p>
   * If false is returned, then the "usage" plugin.yml entry for this command (if defined) will be
   * sent to the player.
   *
   * @param sender  Source of the command
   * @param command Command which was executed
   * @param label   Alias of the command which was used
   * @param args    Passed command arguments
   * @return true if a valid command, otherwise false
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (args.length == 0) {
      return false;
    }

    switch (args[0]) {
      case "save" -> getNpcConfig().saveNpcMap();
      case "reload" -> getNpcConfig().loadNpcMap();
      default -> {
        return false;
      }
    }

    sender.sendMessage(getBundleString("configUpdated"));
    return true;
  }
}
