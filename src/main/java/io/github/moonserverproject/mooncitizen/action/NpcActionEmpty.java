package io.github.moonserverproject.mooncitizen.action;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundle;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcActionEmpty implements NpcAction {

  private boolean isRight = true;
  private boolean isLeft = true;

  @Override
  public String getName() {
    return "EMPTY";
  }

  @Override
  public List<Object> getData() {
    return new ArrayList<>();
  }

  @Override
  public void setData(List<Object> data) {
  }

  @Override
  public void perform(Plugin plugin, Player player) {
  }

  @Override
  public boolean isRight() {
    return isRight;
  }

  @Override
  public void setRight(boolean right) {
    isRight = right;
  }

  @Override
  public boolean isLeft() {
    return isLeft;
  }

  @Override
  public void setLeft(boolean left) {
    isLeft = left;
  }

  /**
   * Converts strings from config/chat to action data
   *
   * @param argList config/chat data
   * @return action data
   */
  @Override
  public List<Object> deserialize(List<Object> argList) {
    return new ArrayList<>(argList);
  }

  /**
   * Converts action data to strings from config
   *
   * @param argList config/chat data
   * @return action data
   */
  @Override
  public List<Object> serialize(List<Object> argList) {
    return deserialize(argList);
  }

  @Override
  public String toString() {
    return MessageFormat.format(getBundle().getString("npcActionInfo"),
        getBundle().getString("npcActionInfoDelimiter"), getName(), isRight, isLeft,
        serialize(getData()));
  }
}
