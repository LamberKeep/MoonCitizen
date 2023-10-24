package io.github.moonserverproject.mooncitizen;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.moonserverproject.mooncitizen.command.CommandHandler;
import io.github.moonserverproject.mooncitizen.config.NpcConfig;
import io.github.moonserverproject.mooncitizen.listener.NpcInteractListener;
import io.github.moonserverproject.mooncitizen.runnable.NpcRunnable;
import java.util.Locale;
import java.util.ResourceBundle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoonCitizen extends JavaPlugin {

  public static final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacy(
      LegacyComponentSerializer.AMPERSAND_CHAR);
  private static int renderDistance;
  private static ProtocolManager protocolManager;
  private static NpcConfig npcConfig;
  private static ResourceBundle bundle;

  /**
   * Returns locale bundle.
   *
   * @return locale bundle
   * @see MoonCitizen#getBundleString(String)
   */
  public static ResourceBundle getBundle() {
    return bundle;
  }


  /**
   * Returns deserialized locale bundle component.
   *
   * @return text component
   */
  public static Component getBundleString(String key) {
    return serializer.deserialize(bundle.getString(key));
  }

  /**
   * Returns NPC render distance.
   *
   * @return render distance (in blocks)
   */
  public static int getRenderDistance() {
    return renderDistance;
  }

  public static NpcConfig getNpcConfig() {
    return npcConfig;
  }

  public static ProtocolManager getProtocolManager() {
    return protocolManager;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    bundle = ResourceBundle.getBundle("messages",
        Locale.forLanguageTag(getConfig().getString("locale", "en")));

    npcConfig = new NpcConfig(this);
    renderDistance = getConfig().getInt("render-distance");
    protocolManager = ProtocolLibrary.getProtocolManager();
    protocolManager.addPacketListener(new NpcInteractListener(this));

    new CommandHandler(this);

    getServer().getScheduler().runTaskTimerAsynchronously(this, new NpcRunnable(), 0, 2);

    getLogger().info(bundle.getString("ready"));
  }

  @Override
  public void onDisable() {
    if (getConfig().getBoolean("auto-save")) {
      getNpcConfig().saveNpcMap();
    }
  }
}
