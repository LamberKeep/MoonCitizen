package io.github.moonserverproject.mooncitizen.command.tabcompleter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonCitizenTabCompleter implements TabCompleter {

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

    if (!sender.hasPermission("mooncitizen.plugin")) {
      options.add("");
      return options;
    }

    if (args.length < 2) {
      options.add("save");
      options.add("reload");
    }

    options.removeIf(s -> !s.contains(args[args.length - 1]));
    return options;
  }
}
