package io.github.moonserverproject.mooncitizen.action;

public class NpcActionFactory {

  /**
   * Returns NPC action by name.
   *
   * @param name action name
   * @return action
   */
  public static NpcAction getAction(String name) {
    return switch (name) {
      case "RUN_COMMAND" -> new NpcActionRunCommand();
      case "MESSAGE" -> new NpcActionMessage();
      case "TELEPORT" -> new NpcActionTeleport();
      default -> new NpcActionEmpty();
    };
  }
}
