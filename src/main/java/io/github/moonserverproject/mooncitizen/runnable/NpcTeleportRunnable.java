package io.github.moonserverproject.mooncitizen.runnable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NpcTeleportRunnable implements Runnable {

  private final Player player;
  private final Location location;

  public NpcTeleportRunnable(Player player, Location location) {
    this.player = player;
    this.location = location;
  }

  /**
   * When an object implementing interface {@code Runnable} is used to create a thread, starting the
   * thread causes the object's {@code run} method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method {@code run} is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    player.teleport(location);
  }
}
