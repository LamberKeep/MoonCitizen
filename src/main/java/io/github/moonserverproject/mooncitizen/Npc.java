package io.github.moonserverproject.mooncitizen;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundle;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getProtocolManager;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getRenderDistance;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.serializer;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import io.github.moonserverproject.mooncitizen.action.NpcAction;
import io.github.moonserverproject.mooncitizen.config.NpcConfig;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class Npc {

  private final int id;
  private final UUID uuid;
  private final HashMap<Integer, NpcAction> actions;
  private final List<Player> players;
  private Component name;
  private EntityType type;
  private boolean invisible;
  private boolean followPlayer;
  private Location location;

  public Npc(int id, UUID uuid, HashMap<Integer, NpcAction> actions, Component name,
      EntityType type,
      Location location, boolean invisible, boolean followPlayer,
      List<Player> players) {
    this.id = id;
    this.uuid = uuid;
    this.actions = actions;
    this.name = name;
    this.type = type;
    this.location = location;
    this.invisible = invisible;
    this.followPlayer = followPlayer;
    this.players = players;
  }

  /**
   * Writes watcher data objects to packet.
   *
   * @param watcher         watcher
   * @param packetContainer packet
   * @see <a
   * href="https://github.com/Nathat23/StackMob-5/blob/master/src/main/java/uk/antiperson/stackmob/hook/hooks/ProtocolLibHook.java#L43">Source</a>
   */
  public static void writeWatchableObjects(WrappedDataWatcher watcher,
      PacketContainer packetContainer) {
    List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

    watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
      WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
      wrappedDataValueList.add(
          new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(),
              entry.getRawValue()));
    });

    packetContainer.getDataValueCollectionModifier().write(0, wrappedDataValueList);
  }

  /**
   * Finds the yaw by location from one entity/block location to another.
   *
   * @param fromLocation the location of the starting object
   * @param toLocation   the location of the target object
   * @return the yaw angle in degrees from fromLocation to toLocation
   * @see Npc#getYawFromLocation(Vector)
   */
  public static double getYawFromLocation(Location fromLocation, Location toLocation) {
    return getYawFromLocation(toLocation.toVector().subtract(fromLocation.toVector()).normalize());
  }

  /**
   * Finds the yaw by location from one entity/block location to another.
   *
   * @param vector vector
   * @return the yaw angle in degrees from fromLocation to toLocation
   * @see <a
   * href="https://www.spigotmc.org/threads/help-with-npcs-head-rotation.427251/#post-3744343">Source</a>
   */
  public static double getYawFromLocation(Vector vector) {
    return Math.toDegrees(Math.atan2(vector.getZ(), vector.getX()) - Math.PI / 2);
  }

  @Override
  public String toString() {
    Function<Double, BigDecimal> format = d -> BigDecimal.valueOf(d)
        .setScale(1, RoundingMode.CEILING);

    return MessageFormat.format(
        getBundle().getString("npcInfo"),
        getBundle().getString("npcInfoDelimiter"), id, uuid, actions.size(), players.size(),
        serializer.serializeOrNull(name), type,
        invisible,
        followPlayer, format.apply(location.getX()), format.apply(location.getY()),
        format.apply(location.getZ()), location.getWorld().getName(),
        format.apply((double) location.getYaw()), format.apply((double) location.getPitch()));
  }

  /**
   * Spawns NPC for specify player.
   *
   * @param player player
   */
  public void spawn(Player player) {
    PacketContainer spawnEntityPacket = new PacketContainer(Server.SPAWN_ENTITY);

    spawnEntityPacket.getIntegers()
        .write(0, id);

    spawnEntityPacket.getUUIDs()
        .write(0, uuid);

    spawnEntityPacket.getEntityTypeModifier()
        .write(0, type);

    spawnEntityPacket.getDoubles()
        .write(0, location.getX())
        .write(1, location.getY())
        .write(2, location.getZ());

    byte yaw = (byte) (location.getYaw() / (360.0F / 256.0F));
    spawnEntityPacket.getBytes()
        .write(0, (byte) (location.getPitch() / (360.0F / 256.0F)))
        .write(1, yaw)
        .write(2, yaw);

    getProtocolManager().sendServerPacket(player, spawnEntityPacket);
    updateMetadata(player, name, invisible);
  }

  public void updateMetadata(Player player, Component displayName, boolean invisible) {
    PacketContainer setEntityDataPacket = new PacketContainer(Server.ENTITY_METADATA);

    setEntityDataPacket.getModifier().writeDefaults();

    setEntityDataPacket.getIntegers()
        .write(0, id);

    WrappedDataWatcher metadata = new WrappedDataWatcher();

    metadata.setObject(new WrappedDataWatcherObject(0,
        WrappedDataWatcher.Registry.get(Byte.class)), invisible ? (byte) 0x20 : (byte) 0x00);

    if (displayName != null) {
      metadata.setObject(new WrappedDataWatcherObject(2,
              WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
          Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson()
              .serialize(displayName)).getHandle()));

      metadata.setObject(new WrappedDataWatcherObject(3,
          WrappedDataWatcher.Registry.get(Boolean.class)), true);
    } else {
      metadata.setObject(new WrappedDataWatcherObject(3,
          WrappedDataWatcher.Registry.get(Boolean.class)), false);
    }

    writeWatchableObjects(metadata, setEntityDataPacket);

    getProtocolManager().sendServerPacket(player, setEntityDataPacket);
  }

  /**
   * Rotates NPC's head to location for specify player.
   *
   * @param player   player
   * @param location npc head looking location
   */
  public void rotateHead(Player player, Location location) {
    PacketContainer headRotationPacket = new PacketContainer(Server.ENTITY_HEAD_ROTATION);

    headRotationPacket.getIntegers()
        .write(0, id);

    headRotationPacket.getBytes()
        // NOTE: Here you can save the Npc's head position and not spam packets while is standing still (but why?)
        .write(0, (byte) (getYawFromLocation(this.location, location) / (360.0F / 256.0F)));

    getProtocolManager().sendServerPacket(player, headRotationPacket);
  }

  /**
   * Moves NPC to location for specify player.
   * <p>
   * To update NPC location for whole server use {@link Npc#setLocation(Location)} method.
   *
   * @param player   player
   * @param location new npc location
   */
  public void move(Player player, Location location) {
    PacketContainer posPacket = new PacketContainer(Server.ENTITY_TELEPORT);

    posPacket.getIntegers()
        .write(0, id);

    posPacket.getDoubles()
        .write(0, location.getX())
        .write(1, location.getY())
        .write(2, location.getZ());

    getProtocolManager().sendServerPacket(player, posPacket);
  }

  /**
   * Destroys (removes) NPC for specify player.
   * <p>
   * To delete NPC use {@link NpcConfig#deleteNpc(Npc)} method.
   *
   * @param player player
   */
  public void destroy(Player player) {
    PacketContainer destroyPacket = new PacketContainer(Server.ENTITY_DESTROY);

    destroyPacket.getIntLists()
        .write(0, Collections.singletonList(id));

    getProtocolManager().sendServerPacket(player, destroyPacket);
  }

  /**
   * Respawn NPC for specify player.
   * <p>
   * To respawn an NPC for all players in render distance use {@link Npc#respawn()} method.
   */
  public void respawn(Player player) {
    destroy(player);
    spawn(player);
  }

  /**
   * Respawn NPC for all players in render distance.
   */
  public void respawn() {
    for (Player nearbyPlayer : location.getNearbyPlayers(getRenderDistance())) {
      respawn(nearbyPlayer);
    }
  }

  public int getId() {
    return id;
  }

  public UUID getUuid() {
    return uuid;
  }

  public HashMap<Integer, NpcAction> getActions() {
    return actions;
  }

  public Component getName() {
    return name;
  }

  public void setName(Component name) {
    this.name = name;
    respawn();
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
    respawn();
  }

  public boolean isInvisible() {
    return invisible;
  }

  public void setInvisible(boolean invisible) {
    this.invisible = invisible;
    respawn();
  }

  public boolean isFollowPlayer() {
    return followPlayer;
  }

  public void setFollowPlayer(boolean followPlayer) {
    this.followPlayer = followPlayer;
    respawn();
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Location getLocation() {
    return location;
  }

  /**
   * Changes NPC location for whole server.
   *
   * @param location new location
   */
  public void setLocation(Location location) {
    // Create new list containing all players in render distance of old and new location
    Set<Player> playerList = new HashSet<>(
        this.location.getNearbyPlayers(getRenderDistance()).stream().toList());
    playerList.addAll(location.getNearbyPlayers(getRenderDistance()));

    // Teleport NPC for all players in list
    for (Player player : playerList) {
      move(player, location);
    }

    // Update NPC location
    this.location = location;

    respawn();
  }

}
