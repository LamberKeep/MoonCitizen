package io.github.moonserverproject.mooncitizen.listener;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getNpcConfig;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import io.github.moonserverproject.mooncitizen.Npc;
import io.github.moonserverproject.mooncitizen.action.NpcAction;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcInteractListener extends PacketAdapter {

  private final HashMap<Player, Integer> cooldownMap = new HashMap<>(); // Cooldowns
  private final Integer cooldown = plugin.getConfig().getInt("cooldown"); // Cooldown delay

  public NpcInteractListener(Plugin plugin) {
    super(plugin, ListenerPriority.NORMAL, Client.USE_ENTITY);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    Player player = event.getPlayer();
    Npc npc = getNpcConfig().getNpc(event.getPacket().getIntegers().getValues().get(0));

    if (npc == null || cooldownMap.getOrDefault(player, 0) > Bukkit.getCurrentTick()) {
      return;
    }

    boolean isRight = event.getPacket().getEnumEntityUseActions().getValues().get(0).getAction()
        == EntityUseAction.ATTACK;
    for (NpcAction action : npc.getActions().values()) {
      if ((isRight && action.isRight()) || (!isRight && action.isLeft())) {
        action.perform(plugin, player);
      }
    }

    cooldownMap.put(player, Bukkit.getCurrentTick() + cooldown);
  }
}
