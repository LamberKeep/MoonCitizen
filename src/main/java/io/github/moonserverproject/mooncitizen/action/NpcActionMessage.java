package io.github.moonserverproject.mooncitizen.action;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundle;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcActionMessage extends NpcActionEmpty {

  private Component message;

  @Override
  public String getName() {
    return "MESSAGE";
  }

  @Override
  public List<Object> getData() {
    return Collections.singletonList(message);
  }

  @Override
  public void setData(List<Object> data) {
    message = (Component) data.get(0);
  }

  @Override
  public void perform(Plugin plugin, Player player) {
    if (message == null) {
      plugin.getLogger().warning(getBundle().getString("npcActionMessageNotSet"));
      return;
    }
    player.sendMessage(message);
  }

  @Override
  public List<Object> deserialize(List<Object> argList) {
    List<Object> dataList = new ArrayList<>(argList);
    dataList.set(0, serializer.deserializeOrNull((String) argList.get(0)));
    return dataList;
  }

  @Override
  public List<Object> serialize(List<Object> argList) {
    List<Object> dataList = new ArrayList<>(argList);
    dataList.set(0, serializer.serializeOrNull((Component) argList.get(0)));
    return dataList;
  }
}
