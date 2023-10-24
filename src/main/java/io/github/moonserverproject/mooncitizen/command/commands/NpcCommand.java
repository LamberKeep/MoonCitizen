package io.github.moonserverproject.mooncitizen.command.commands;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundleString;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getNpcConfig;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.serializer;

import io.github.moonserverproject.mooncitizen.Npc;
import io.github.moonserverproject.mooncitizen.action.NpcAction;
import io.github.moonserverproject.mooncitizen.action.NpcActionFactory;
import io.github.moonserverproject.mooncitizen.action.NpcActionTeleport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NpcCommand implements CommandExecutor {

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
    if (!(sender instanceof Player player)) {
      sender.sendMessage(getBundleString("commandPlayer"));
      return true;
    }

    if (args.length < 2) {
      return false;
    }

    int npcId;

    try {
      npcId = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      player.sendMessage(getBundleString("commandUnparsableInteger"));
      return true;
    }

    Npc npc = getNpcConfig().getNpc(npcId);

    if ("create".equals(args[1])) {
      if (npc != null) {
        player.sendMessage(getBundleString("npcExists"));
        return true;
      }
    } else {
      if (npc == null) {
        player.sendMessage(getBundleString("npcNotExists"));
        return true;
      }
    }

    try {
      switch (args[1]) {
        case "info" -> {
          sender.sendMessage(Component.text(npc.toString()).color(TextColor.color(0xFFFF55)));
          return true;
        }
        case "create" ->
            npc = new Npc(npcId, UUID.randomUUID(), new HashMap<>(), null, EntityType.ARMOR_STAND,
                player.getLocation(), false, false, new ArrayList<>());
        case "remove" -> npc = null;
        case "move" -> npc.setLocation(player.getLocation());
        case "name" -> npc.setName(args[2].equals("\"\"") ? null : serializer.deserialize(args[2]));
        case "type" -> {
          try {
            npc.setType(EntityType.valueOf(args[2].toUpperCase()));
          } catch (IllegalArgumentException e) {
            sender.sendMessage(getBundleString("npcEntityTypeNotExists"));
            return true;
          }
        }
        case "invisible" -> npc.setInvisible(Boolean.parseBoolean(args[2]));
        case "follow" -> npc.setFollowPlayer(Boolean.parseBoolean(args[2]));
        case "action" -> {
          int actionId;

          try {
            actionId = Integer.parseInt(args[2]);
          } catch (NumberFormatException e) {
            player.sendMessage(getBundleString("commandUnparsableInteger"));
            return true;
          }

          NpcAction action = npc.getActions().get(actionId);

          if ("create".equals(args[3])) {
            if (action != null) {
              player.sendMessage(getBundleString("npcActionExists"));
              return true;
            }
          } else {
            if (action == null) {
              player.sendMessage(getBundleString("npcActionNotExists"));
              return true;
            }
          }

          switch (args[3]) {
            case "create" -> action = NpcActionFactory.getAction(args[4].toUpperCase());
            case "right" -> action.setRight(Boolean.parseBoolean(args[4]));
            case "left" -> action.setLeft(Boolean.parseBoolean(args[4]));
            case "data" -> action.setData(action instanceof NpcActionTeleport
                ? Collections.singletonList(player.getLocation())
                : action.deserialize(
                    List.of(args).subList(4, args.length).stream().map(s -> (Object) s).toList()));
            case "remove" -> action = null;
            case "info" -> {
              player.sendMessage(Component.text(npc.getActions().get(actionId).toString())
                  .color(TextColor.color(0xFFFF55)));
              return true;
            }
            default -> {
              return false;
            }
          }

          if (action == null) {
            npc.getActions().remove(actionId);
            player.sendMessage(getBundleString("npcActionRemoved"));
            return true;
          }

          if (!npc.getActions().containsKey(actionId)) {
            npc.getActions().put(actionId, action);
            player.sendMessage(getBundleString("npcActionCreated"));
            return true;
          }

          player.sendMessage(getBundleString("npcActionUpdated"));
          return true;
        }
        default -> {
          return false;
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    }

    if (npc == null) {
      getNpcConfig().deleteNpc(npcId);
      player.sendMessage(getBundleString("npcRemoved"));
      return true;
    }

    if (!getNpcConfig().getNpcMap().containsKey(npcId)) {
      getNpcConfig().updateNpc(npc);
      player.sendMessage(getBundleString("npcCreated"));
      return true;
    }

    player.sendMessage(getBundleString("npcUpdated"));
    return true;
  }
}
