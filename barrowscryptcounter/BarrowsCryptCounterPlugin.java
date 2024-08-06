package net.runelite.client.plugins.barrowscryptcounter;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.slf4j.LoggerFactory;
import net.runelite.client.events.ConfigChanged;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(
        name = "Barrows Crypt Counter",
        description = "Tracks the number of small NPCs killed in the Barrows basement",
        tags = {"barrows", "counter"}
)
public class BarrowsCryptCounterPlugin extends Plugin {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(BarrowsCryptCounterPlugin.class);
    private static final long INACTIVITY_THRESHOLD_MS = 900000; // 15 minutes inactivity threshold
    private static final int BARROWS_REGION_ID = 14131; // Example region ID for Barrows
    private static final int BARROWS_CRYPT_REGION_ID = 14231; // Example region ID for Barrows Crypt
    private static final String KILL_COUNTS_FILE = "barrows_kill_counts.dat";

    private Map<String, Integer> killCounts;
    private List<String> npcList;
    private Instant lastActivityTime;
    private int lastRewardPotential = 0;

    @Inject
    private Client client;

    @Inject
    private BarrowsCryptCounterPanelOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BarrowsCryptCounterConfig config;

    @Inject
    private ConfigManager configManager;

    @Provides
    BarrowsCryptCounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarrowsCryptCounterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        killCounts = new HashMap<>();
        npcList = new ArrayList<>();
        initializeNpcList();
        loadKillCountsFromFile();
        resetActivityTimer();
        if (overlayManager != null && overlay != null) {
            overlayManager.add(overlay);
        }
        updateOverlay();

        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ERROR);

        logger.info("BarrowsCryptCounterPlugin started.");
    }

    @Override
    protected void shutDown() throws Exception {
        saveKillCountsToFile();
        if (killCounts != null) {
            killCounts.clear();
        }
        if (npcList != null) {
            npcList.clear();
        }
        if (overlayManager != null && overlay != null) {
            overlayManager.remove(overlay);
        }
        logger.info("BarrowsCryptCounterPlugin stopped.");
    }

    private void initializeNpcList() {
        npcList.clear();
        npcList.add("Crypt rat");
        npcList.add("Bloodworm");
        npcList.add("Crypt spider");
        npcList.add("Giant crypt rat");
        npcList.add("Skeleton");
        npcList.add("Giant crypt spider");
        // Add Barrows brothers to the list
        npcList.add("Ahrim");
        npcList.add("Dharok");
        npcList.add("Guthan");
        npcList.add("Karil");
        npcList.add("Torag");
        npcList.add("Verac");
        logger.info("Initialized NPC list.");
    }

    private void saveKillCountsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(KILL_COUNTS_FILE))) {
            out.writeObject(killCounts);
            logger.info("Kill counts saved to file.");
        } catch (IOException e) {
            logger.error("Failed to save kill counts to file.", e);
        }
    }

    private void loadKillCountsFromFile() {
        File file = new File(KILL_COUNTS_FILE);
        if (!file.exists()) {
            logger.info("Kill counts file not found, starting fresh.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(KILL_COUNTS_FILE))) {
            killCounts = (Map<String, Integer>) in.readObject();
            logger.info("Kill counts loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to load kill counts from file.", e);
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null || event.getActor() == null || event.getActor().getName() == null) {
            return;
        }

        String npcName = Text.removeTags(event.getActor().getName());
        if (isSmallBarrowsNPC(npcName) && event.getActor().getInteracting() == client.getLocalPlayer()) {
            killCounts.put(npcName, killCounts.getOrDefault(npcName, 0) + 1);
            logger.info("Killed: " + npcName);

            updateOverlay();
            resetActivityTimer(); // Reset timer on activity
        }
    }

    private boolean isSmallBarrowsNPC(String npcName) {
        return npcList.contains(npcName);
    }

    private void updateOverlay() {
        if (client == null || client.getGameState() != GameState.LOGGED_IN) {
            logger.error("Client is not ready when updating overlay.");
            return;
        }

        logger.info("Updating overlay...");
        overlay.updatePanel();
    }

    private int getRewardPotential() {
        return client.getVarbitValue(Varbits.BARROWS_REWARD_POTENTIAL);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN) {
            logger.error("Client is not ready when widget loaded.");
            return;
        }

        if (event.getGroupId() == InterfaceID.BARROWS_REWARD) {
            resetKillCounts(); // Reset on chest opening
            resetRewardPotential(); // Reset reward potential
            resetActivityTimer(); // Reset timer on chest opening
            updateOverlay(); // Ensure overlay is updated
            logger.info("Widget loaded, reset kill counts and reward potential.");
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN) {
            // logger.error("Client is not ready when game object spawned.");
            return;
        }

        // Check if the player has spawned into the Barrows crypt
        int regionID = event.getGameObject().getWorldLocation().getRegionID();
        if (regionID == BARROWS_CRYPT_REGION_ID) {
            if (overlayManager != null && overlay != null) {
                overlayManager.add(overlay);
            }
            resetActivityTimer(); // Reset timer when entering Barrows area
            updateOverlay();
            logger.info("Player entered Barrows crypt, overlay added and activity timer reset.");
        }
    }

    private void resetKillCounts() {
        if (killCounts != null) {
            killCounts.clear();
        }
        updateOverlay(); // Ensure overlay is updated after resetting counts
        logger.info("Kill counts reset.");
    }

    private void resetRewardPotential() {
        if (client != null && client.getGameState() == GameState.LOGGED_IN) {
            try {
                client.setVarbit(Varbits.BARROWS_REWARD_POTENTIAL, 0);
                logger.info("Reward potential reset.");
            } catch (NullPointerException e) {
                logger.error("Error resetting reward potential, client or varbit is null.", e);
            }
        } else {
            logger.error("Client is not ready when resetting reward potential.");
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            if (overlayManager != null && overlay != null) {
                overlayManager.remove(overlay);
            }
            logger.info("Game state changed to LOGIN_SCREEN, removed overlay.");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN) {
            logger.error("Client is not ready during game tick.");
            return;
        }

        if (isInBarrowsArea()) {
            if (overlayManager != null && overlay != null) {
                overlayManager.add(overlay);
            }
            int currentRewardPotential = getRewardPotential();
            if (currentRewardPotential < lastRewardPotential) {
                resetKillCounts(); // Reset kill counts if reward potential has been reset
            }
            lastRewardPotential = currentRewardPotential;
            resetActivityTimer(); // Reset timer when entering Barrows area
            updateOverlay();
            logger.info("Player is in Barrows area, overlay added and activity timer reset.");
        } else if (isInactive()) {
            if (overlayManager != null && overlay != null) {
                overlayManager.remove(overlay);
            }
            logger.info("Player is inactive, overlay removed.");
        } else {
            logger.info("Player is active but not in Barrows area.");
        }
    }

    private void resetActivityTimer() {
        lastActivityTime = Instant.now();
        logger.info("Activity timer reset.");
    }

    private boolean isInactive() {
        boolean inactive = Instant.now().isAfter(lastActivityTime.plusMillis(INACTIVITY_THRESHOLD_MS));
        if (inactive) {
            logger.info("Player is inactive.");
        }
        return inactive;
    }

    private boolean isInBarrowsArea() {
        if (client == null || client.getLocalPlayer() == null) {
            logger.error("Client or local player is null when checking Barrows area.");
            return false;
        }

        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (playerLocation == null) {
            logger.error("Player location is null when checking Barrows area.");
            return false;
        }

        int regionID = playerLocation.getRegionID();
        logger.info("Current Region ID: {}", regionID);
        return regionID == BARROWS_REGION_ID || regionID == BARROWS_CRYPT_REGION_ID;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN) {
            logger.error("Client is not ready when config changed.");
            return;
        }

        if (event.getGroup().equals("barrowscryptcounter") && event.getKey().equals("resetKillCounts")) {
            handleResetKillCounts();
        } else {
            updateOverlay(); // Update the overlay when the config changes
            resetActivityTimer(); // Reset timer on config change
            logger.info("Config changed, overlay updated and activity timer reset.");
        }
    }

    private void handleResetKillCounts() {
        Window parentWindow = SwingUtilities.getWindowAncestor(client.getCanvas());
        int result = JOptionPane.showConfirmDialog(
                parentWindow,
                "Are you sure you want to reset the kill counts?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.NO_OPTION) {
            logger.info("Kill counts reset cancelled by user.");
            return;
        }

        resetKillCounts();
        saveKillCountsToFile();  // Save the reset counts to the file
        logger.info("Kill counts reset by user.");
    }

    public int getTargetCountForNpc(String npcName) {
        switch (npcName) {
            case "Crypt rat":
                return config.targetCryptRat();
            case "Bloodworm":
                return config.targetBloodworm();
            case "Crypt spider":
                return config.targetCryptSpider();
            case "Giant crypt rat":
                return config.targetGiantCryptRat();
            case "Skeleton":
                return config.targetSkeleton();
            case "Giant crypt spider":
                return config.targetGiantCryptSpider();
            case "Ahrim":
                return config.targetAhrim();
            case "Dharok":
                return config.targetDharok();
            case "Guthan":
                return config.targetGuthan();
            case "Karil":
                return config.targetKaril();
            case "Torag":
                return config.targetTorag();
            case "Verac":
                return config.targetVerac();
            default:
                return 0;
        }
    }

    public Map<String, Integer> getKillCounts() {
        return killCounts;
    }

    public List<String> getNpcList() {
        return npcList;
    }
}
