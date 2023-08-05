package com.barrowscryptcounter;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
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
    // Create a data structure to store the kill counts of small NPCs
    private Map<String, Integer> killCounts;
    private List<String> npcList;

    private static final Logger logger = LoggerFactory.getLogger(BarrowsCryptCounterPlugin.class);

    @Inject
    private Client client;

    @Inject
    private BarrowsCryptCounterPanelOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Override
    protected void startUp() throws Exception {
        // Initialize the data structure and any other setup tasks
        killCounts = new HashMap<>();
        npcList = new ArrayList<>();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        // Perform any cleanup or saving of data when the plugin is disabled
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        // Check if the dead actor is a small NPC in the Barrows basement
        if (isSmallBarrowsNPC(event.getActor().getName())) {
            // Get the local player
            Actor localPlayer = client.getLocalPlayer();

            // Check if the local player caused the death
            if (event.getActor().getInteracting() == localPlayer) {
                String npcName = event.getActor().getName();
                killCounts.compute(npcName, (name, currentKills) -> (currentKills == null) ? 1 : currentKills + 1);
                logger.info("Killed: " + Text.removeTags(npcName));

                // Add the NPC to the list of killed NPCs if not already there
                if (!npcList.contains(npcName)) {
                    npcList.add(npcName);
                }

                // Call a method to update the overlay with the new kill count and NPC list
                updateOverlay();
            }
        }
    }


    private boolean isSmallBarrowsNPC(String npcName) {
        // Implement this method to check if the NPC is one of the small NPCs in the Barrows basement
        // Return true if it's a small NPC, false otherwise

        // Add the names of all small Barrows NPCs here
        String[] smallBarrowsNPCs = {
                "Crypt rat",
                "Bloodworm",
                "Crypt spider",
                "Giant crypt rat",
                "Skeleton",
                "Giant crypt spider"
        };

        for (String smallNPC : smallBarrowsNPCs) {
            if (npcName.equalsIgnoreCase(smallNPC)) {
                return true;
            }
        }

        return false;
    }

    private void updateOverlay() {
        // Call a method to display the kill count and NPC list overlay on the screen
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            // Clear the kill count and NPC list when the player logs out or hops worlds
            killCounts.clear();
            npcList.clear();
        }
    }

    public Map<String, Integer> getKillCounts() {
        return killCounts;
    }

    public List<String> getNpcList() {
        return npcList;
    }
}
