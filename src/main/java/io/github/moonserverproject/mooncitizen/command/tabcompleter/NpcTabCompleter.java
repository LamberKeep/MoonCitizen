package io.github.moonserverproject.mooncitizen.command.tabcompleter;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundle;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getNpcConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NpcTabCompleter implements TabCompleter {

  /**
   * Requests a list of possible completions for a command argument.
   *
   * @param sender  Source of the command.  For players tab-completing a command inside of a command
   *                block, this will be the player, not the command block.
   * @param command Command which was executed
   * @param label   Alias of the command which was used
   * @param args    The arguments passed to the command, including final partial argument to be
   *                completed
   * @return A List of possible completions for the final argument, or null to default to the
   * command executor
   */
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
      @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    List<String> options = new ArrayList<>();

    if (!sender.hasPermission("mooncitizen.npc")) {
      options.add("");
      return options;
    }

    switch (args.length) {
      case 0, 1 -> options.addAll(
          getNpcConfig().getNpcMap().keySet().parallelStream().map(String::valueOf).toList());
      case 2 -> Collections.addAll(options, "info", "create", "remove", "move", "name", "type",
          "invisible", "follow", "action");
      case 3 -> {
        if (args[1].equals("action")) {
          options.addAll(getNpcConfig().getNpc(Integer.parseInt(args[0])).getActions().keySet()
              .parallelStream().map(String::valueOf).toList());
        } else {
          options.add(getBundle().getString("commandParam"));
        }
      }

      default -> options.add(getBundle().getString("commandParam"));
    }

    options.removeIf(s -> !s.contains(args[args.length - 1]));
    return options;
  }
}
