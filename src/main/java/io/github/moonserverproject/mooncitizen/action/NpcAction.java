package io.github.moonserverproject.mooncitizen.action;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface NpcAction {

  /**
   * Returns NPC action name
   *
   * @return NPC action name
   */
  String getName();

  /**
   * Returns NPC action data
   *
   * @return NPC action name
   */
  List<Object> getData();

  /**
   * Sets NPC action data
   *
   * @param data NPC action data
   */
  void setData(List<Object> data);

  /**
   * Performs an action on a specific player.
   *
   * @param plugin plugin
   * @param player specific player
   */
  void perform(Plugin plugin, Player player);

  /**
   * Returns a value indicating whether this event can be triggered with the right mouse button.
   *
   * @return action can be triggered by right mouse button
   */
  boolean isRight();

  /**
   * Sets whether this action can be triggered with the right mouse button.
   *
   * @param right action can be triggered by right mouse button
   */
  void setRight(boolean right);

  /**
   * Returns a value indicating whether this event can be triggered with the left mouse button.
   *
   * @return action can be triggered by left mouse button
   */
  boolean isLeft();

  /**
   * Sets whether this action can be triggered with the left mouse button.
   *
   * @param left action can be triggered with the left mouse button
   */
  void setLeft(boolean left);

  /**
   * Converts strings from config/chat to action data
   *
   * @param argList config/chat data
   * @return action data
   */
  List<Object> deserialize(List<Object> argList);

  /**
   * Converts action data to config strings
   *
   * @param argList config/chat data
   * @return action data
   */
  List<Object> serialize(List<Object> argList);
}
