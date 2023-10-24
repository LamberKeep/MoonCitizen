package io.github.moonserverproject.mooncitizen.config;

import static io.github.moonserverproject.mooncitizen.MoonCitizen.getBundle;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.getRenderDistance;
import static io.github.moonserverproject.mooncitizen.MoonCitizen.serializer;

import io.github.moonserverproject.mooncitizen.Npc;
import io.github.moonserverproject.mooncitizen.action.NpcAction;
import io.github.moonserverproject.mooncitizen.action.NpcActionFactory;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NpcConfig {

  private final Plugin plugin;
  private final FileConfiguration config;
  private final File configFile;

  private final HashMap<Integer, Npc> npcMap;

  public NpcConfig(Plugin plugin) {
    this.plugin = plugin;
    this.configFile = new File(plugin.getDataFolder(), "npc.yml");

    if (!configFile.exists()) {
      if (configFile.getParentFile().mkdirs()) {
        plugin.getLogger().info(
            MessageFormat.format(getBundle().getString("configCreatedDirectory"),
                configFile.getPath()));
      }

      try {
        //noinspection BlockingMethodInNonBlockingContext
        if (configFile.createNewFile()) {
          plugin.getLogger().info(
              MessageFormat.format(getBundle().getString("configCreated"), configFile.getName()));
        }
      } catch (IOException e) {
        plugin.getLogger().info(
            MessageFormat.format(getBundle().getString("configNotCreated"), configFile.getName()));
        plugin.getServer().getPluginManager().disablePlugin(plugin);
      }
    }

    this.config = YamlConfiguration.loadConfiguration(configFile);
    this.npcMap = loadNpcMap(); // Make sure that this line is located after loading the config
  }

  /**
   * Searches NPC by entity ID.
   *
   * @return Npc or null if not found.
   */
  public Npc getNpc(int id) {
    return npcMap.get(id);
  }

  public HashMap<Integer, Npc> getNpcMap() {
    return npcMap;
  }

  /**
   * Adds/updates NPC in list.
   * <p>
   * This method doesn't change file configuration.
   *
   * @param npc Npc record
   * @see NpcConfig#saveNpcMap()
   */
  public void updateNpc(Npc npc) {
    npcMap.put(npc.getId(), npc);
  }

  /**
   * Deletes NPC for whole server.
   *
   * @param id NPC's id
   */
  public void deleteNpc(int id) {
    deleteNpc(getNpc(id));
  }

  /**
   * Deletes NPC for whole server.
   *
   * @param npc NPC
   */
  public void deleteNpc(Npc npc) {
    for (Player nearbyPlayer : npc.getLocation().getNearbyPlayers(getRenderDistance())) {
      npc.destroy(nearbyPlayer);
    }

    npcMap.remove(npc.getId());
  }

  /**
   * Saves NPC configuration.
   *
   * @see NpcConfig#saveNpcMap(HashMap)
   */
  public void saveNpcMap() {
    saveNpcMap(npcMap);
  }

  /**
   * Writes NPC configuration by given list.
   *
   * @param npcMap Npc map
   */
  public void saveNpcMap(HashMap<Integer, Npc> npcMap) {
    // Clear old config
    config.set("npc", null);

    for (Entry<Integer, Npc> npcEntry : npcMap.entrySet()) {
      Npc npc = npcEntry.getValue();
      String prefix = "npc." + npcEntry.getKey() + ".";

      config.set(prefix + "uuid", npc.getUuid().toString());
      config.set(prefix + "name", serializer.serializeOrNull(npc.getName()));
      config.set(prefix + "type", npc.getType().name());
      config.set(prefix + "location", npc.getLocation());
      config.set(prefix + "invisible", npc.isInvisible());
      config.set(prefix + "follow", npc.isFollowPlayer());

      // Set actions list
      for (Entry<Integer, NpcAction> actionEntry : npc.getActions().entrySet()) {
        prefix = "npc." + npcEntry.getKey() + ".actions." + actionEntry.getKey();
        NpcAction action = actionEntry.getValue();
        config.set(prefix + ".name", action.getName());
        config.set(prefix + ".right", action.isRight());
        config.set(prefix + ".left", action.isLeft());
        config.set(prefix + ".data", action.serialize(action.getData()));
      }
    }

    try {
      config.save(configFile);
    } catch (IOException e) {
      plugin.getLogger().warning(getBundle().getString("saveConfigError"));
    }
  }

  /**
   * Loads all NPC contained in config.
   *
   * @return Npc record list
   * @see NpcConfig#loadNpc(int)
   */
  public HashMap<Integer, Npc> loadNpcMap() {
    HashMap<Integer, Npc> npcMap = new HashMap<>();
    ConfigurationSection section = config.getConfigurationSection("npc");

    if (section == null) {
      plugin.getLogger().warning(getBundle().getString("configNpcLoadError"));
      return npcMap; // Empty hashmap
    }

    for (String s : section.getValues(false).keySet()) {
      plugin.getLogger().warning(MessageFormat.format(getBundle().getString("configNpcLoad"), s));
      int id = Integer.parseInt(s);
      npcMap.put(id, loadNpc(id));
    }

    return npcMap;
  }

  /**
   * Loads NPC contained in config.
   *
   * @param id Npc id (entity id)
   * @return Npc record or null if not found
   */
  public Npc loadNpc(int id) {
    ConfigurationSection section = config.getConfigurationSection("npc." + id);

    if (section == null) {
      return null;
    }

    HashMap<Integer, NpcAction> actions = new HashMap<>();
    ConfigurationSection actionsSection = section.getConfigurationSection("actions");

    // Loading NPC actions
    if (actionsSection != null) {
      for (String actionId : actionsSection.getKeys(false)) {
        String actionName = actionsSection.getString(actionId + ".name");

        if (actionName == null) {
          continue;
        }

        NpcAction action = NpcActionFactory.getAction(actionName);
        List<?> actionData = actionsSection.getList(actionId + ".data");
        if (actionData != null) {
          action.setData(action.deserialize(new ArrayList<>(actionData)));
        }

        action.setRight(actionsSection.getBoolean(actionId + ".right"));
        action.setLeft(actionsSection.getBoolean(actionId + ".left"));
        actions.put(Integer.valueOf(actionId), action);
      }
    }

    String uuidString = section.getString("uuid");
    UUID uuid = uuidString == null ? UUID.randomUUID() : UUID.fromString(uuidString);

    return new Npc(
        id,
        uuid,
        actions,
        serializer.deserializeOrNull(section.getString("name")),
        EntityType.valueOf(section.getString("type")),
        section.getLocation("location"),
        section.getBoolean("invisible"),
        section.getBoolean("follow"),
        new ArrayList<>()
    );
  }
}
