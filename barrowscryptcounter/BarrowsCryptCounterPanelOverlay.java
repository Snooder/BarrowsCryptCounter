package net.runelite.client.plugins.barrowscryptcounter;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class BarrowsCryptCounterPanelOverlay extends Overlay {
    private static final Logger logger = LoggerFactory.getLogger(BarrowsCryptCounterPanelOverlay.class);
    private final BarrowsCryptCounterPlugin plugin;
    private final PanelComponent panelComponent;
    private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");
    private final Client client;
    private final BarrowsCryptCounterConfig config;

    @Provides
    BarrowsCryptCounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarrowsCryptCounterConfig.class);
    }

    @Inject
    private BarrowsCryptCounterPanelOverlay(BarrowsCryptCounterPlugin plugin, BarrowsCryptCounterConfig config, Client client) {
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        panelComponent = new PanelComponent();
    }

    public void updatePanel() {
        logger.info("Updating panel...");

        // Clear previous children
        panelComponent.getChildren().clear();

        Map<String, Integer> killCounts = plugin.getKillCounts();
        List<String> npcList = plugin.getNpcList();
        boolean allTargetsMet = true;

        // Determine if all targets are met
        for (String npcName : npcList) {
            int target = plugin.getTargetCountForNpc(npcName);
            int kills = killCounts.getOrDefault(npcName, 0);
            logger.info("NPC: {} Kills: {} Target: {}", npcName, kills, target); // Debug line
            if (kills < target) {
                allTargetsMet = false;
            }
        }

        // Set panel background color based on whether all targets are met
        Color backgroundColor = config.backgroundColor();
        panelComponent.setBackgroundColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), 150));

        // Set text color based on dark mode setting
        Color textColor = config.darkModeText() ? Color.WHITE : Color.BLACK;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Barrows Crypt Counter")
                .leftColor(textColor)
                .build());

        // Add NPC kill counts to the panel, including crypt creatures and Barrows brothers
        for (String npcName : npcList) {
            int target = plugin.getTargetCountForNpc(npcName);
            int kills = killCounts.getOrDefault(npcName, 0);
            if (target == 0 && kills == 0) {
                continue; // Skip NPCs with target and kills both at 0
            }
            if (target == 0 && kills > 0) {
                String lineText = npcName + ": " + kills + "/" + target;
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(lineText)
                        .leftColor(Color.RED)
                        .build());
                continue;
            }
            String lineText = npcName + ": " + kills + "/" + target;
            Color npcColor = (kills > target) ? Color.RED : (kills >= target) ? Color.GREEN : textColor; // Set color to red if over target, green if completed
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(lineText)
                    .leftColor(npcColor)
                    .build());
        }

        // Display reward potential if enabled and client is in a valid state
        if (config.showRewardPotential() && client != null && client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null) {
            final int rewardPotential = client.getVarbitValue(Varbits.BARROWS_REWARD_POTENTIAL);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Potential")
                    .right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
                    .leftColor(textColor)
                    .rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
                    .build());
        }
        logger.info("Panel updated.");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client == null || client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            logger.warn("Client is not ready, skipping render.");
            return null;
        }

        logger.info("Rendering panel...");

        Dimension dimension = panelComponent.render(graphics);

        // Draw the custom border
        boolean allTargetsMet = true;
        for (String npcName : plugin.getNpcList()) {
            int target = plugin.getTargetCountForNpc(npcName);
            int kills = plugin.getKillCounts().getOrDefault(npcName, 0);
            if (kills < target) {
                allTargetsMet = false;
                break;
            }
        }

        if (allTargetsMet) {
            Color borderColor = config.completionBorderColor();
            graphics.setColor(borderColor);
            Rectangle bounds = panelComponent.getBounds();
            if (bounds != null) {
                graphics.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
            }
        }

        return dimension;
    }
}
