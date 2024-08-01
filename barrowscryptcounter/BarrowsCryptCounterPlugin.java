package net.runelite.client.plugins.barrowscryptcounter;

import com.google.inject.Provides;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(
        name = "Barrows Counter",
        description = "Tracks the number of small NPCs killed in the Barrows basement",
        tags = {"barrows", "counter"}
)
public class BarrowsCryptCounterPlugin extends Plugin {
    private Map<String, Integer> killCounts;
    private List<String> npcList;

    private static final Logger logger = LoggerFactory.getLogger(BarrowsCryptCounterPlugin.class);

    @Inject
    private Client client;

    @Inject
    private BarrowsCryptCounterPanelOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BarrowsCryptCounterConfig config;

    @Provides
    BarrowsCryptCounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarrowsCryptCounterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        killCounts = new HashMap<>();
        npcList = new ArrayList<>();
        initializeNpcList();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        killCounts.clear();
        npcList.clear();
        overlayManager.remove(overlay);
    }

    private void initializeNpcList() {
        npcList.add("Crypt rat");
        npcList.add("Bloodworm");
        npcList.add("Crypt spider");
        npcList.add("Giant crypt rat");
        npcList.add("Skeleton");
        npcList.add("Giant crypt spider");
        // Add more NPCs here if needed
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (isSmallBarrowsNPC(event.getActor().getName())) {
            Actor localPlayer = client.getLocalPlayer();
            if (event.getActor().getInteracting() == localPlayer) {
                String npcName = event.getActor().getName();
                killCounts.compute(npcName, (name, currentKills) -> (currentKills == null) ? 1 : currentKills + 1);
                logger.info("Killed: " + Text.removeTags(npcName));

                if (!npcList.contains(npcName)) {
                    npcList.add(npcName);
                }

                updateOverlay();
            }
        }
    }

    private boolean isSmallBarrowsNPC(String npcName) {
        return npcList.contains(npcName);
    }

    private void updateOverlay() {
//        setKillCounts(new HashMap<>(killCounts));
//        setNpcList(new ArrayList<>(npcList));
//        overlay.update(); // Ensure the overlay updates with the latest data
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == InterfaceID.BARROWS_REWARD) {
            resetKillCounts(); // Reset on chest opening
        }
    }

    private void resetKillCounts() {
        killCounts.clear();
        initializeNpcList(); // Reinitialize NPC list if necessary
        updateOverlay(); // Ensure overlay is updated after resetting counts
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            killCounts.clear();
            npcList.clear();
        }
    }

    public int getTargetCountForNpc(String npcName) {
        switch (npcName) {
            case "Crypt Rat":
                return config.targetCryptRat();
            case "Bloodworm":
                return config.targetBloodworm();
            case "Crypt Spider":
                return config.targetCryptSpider();
            case "Giant Crypt Rat":
                return config.targetGiantCryptRat();
            case "Skeleton":
                return config.targetSkeleton();
            case "Giant Crypt Spider":
                return config.targetGiantCryptSpider();
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
