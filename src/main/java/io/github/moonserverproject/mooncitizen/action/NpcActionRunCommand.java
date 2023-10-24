package io.github.moonserverproject.mooncitizen.action;

import io.github.moonserverproject.mooncitizen.runnable.NpcRunCommandRunnable;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcActionRunCommand extends NpcActionEmpty {

  private String command;

  @Override
  public String getName() {
    return "RUN_COMMAND";
  }

  @Override
  public List<Object> getData() {
    return Collections.singletonList(command);
  }

  @Override
  public void setData(List<Object> data) {
    command = (String) data.get(0);
  }

  @Override
  public void perform(Plugin plugin, Player player) {
    Bukkit.getScheduler().runTask(plugin, new NpcRunCommandRunnable(player, command));
  }
}
