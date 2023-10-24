package io.github.moonserverproject.mooncitizen.action;

import io.github.moonserverproject.mooncitizen.runnable.NpcTeleportRunnable;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcActionTeleport extends NpcActionEmpty {

  private Location location;

  @Override
  public String getName() {
    return "TELEPORT";
  }

  @Override
  public List<Object> getData() {
    return Collections.singletonList(location);
  }

  @Override
  public void setData(List<Object> data) {
    location = (Location) data.get(0);
  }

  @Override
  public void perform(Plugin plugin, Player player) {
    Bukkit.getScheduler().runTask(plugin, new NpcTeleportRunnable(player, location));
  }
}
