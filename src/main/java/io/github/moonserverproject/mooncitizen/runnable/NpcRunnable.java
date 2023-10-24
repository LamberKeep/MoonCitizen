package io.github.moonserverproject.mooncitizen.runnable;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getNpcConfig;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getRenderDistance;

import io.github.moonserverproject.mooncitizen.Npc;
import java.util.List;
import org.bukkit.entity.Player;

public class NpcRunnable implements Runnable {

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
    for (Npc NPC : getNpcConfig().getNpcMap().values()) {
      List<Player> renderNpcPlayerList = NPC.getPlayers();

      // Remove player in range that leaves from world/server
      renderNpcPlayerList.removeIf(
          player -> !player.isOnline() || !player.getWorld()
              .equals(NPC.getLocation().getWorld()));

      for (Player player : NPC.getLocation().getWorld().getPlayers()) {
        // If the player is not in range
        if (player.getLocation().distance(NPC.getLocation()) > getRenderDistance()) {
          // NOTE: Here you can remove Npc by sending 0x40 packet to client (but why?)
          // The player was in range, but left
          renderNpcPlayerList.remove(player);
          continue;
        }

        // The player has entered the range
        if (!renderNpcPlayerList.contains(player)) {
          NPC.spawn(player);
          renderNpcPlayerList.add(player);
        }

        if (NPC.isFollowPlayer()) {
          NPC.rotateHead(player, player.getLocation());
        }
      }
    }
  }
}
