package io.github.moonserverproject.mooncitizen.runnable;

import org.bukkit.entity.Player;

public class NpcRunCommandRunnable implements Runnable {

  private final Player player;
  private final String command;

  public NpcRunCommandRunnable(Player player, String command) {
    this.player = player;
    this.command = command;
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
    player.performCommand(command);
  }
}
