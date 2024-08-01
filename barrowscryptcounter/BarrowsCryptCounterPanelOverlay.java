package net.runelite.client.plugins.barrowscryptcounter;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BarrowsCryptCounterPanelOverlay extends Overlay {
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
        panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Map<String, Integer> killCounts = plugin.getKillCounts();
        List<String> npcList = plugin.getNpcList();
        boolean allTargetsMet = true;

        panelComponent.getChildren().clear();

        // Determine if all targets are met
        for (String npcName : npcList) {
            int target = plugin.getTargetCountForNpc(npcName);
            if (killCounts.getOrDefault(npcName, 0) < target) {
                allTargetsMet = false;
            }
        }

        // Set panel background color based on whether all targets are met
        Color backgroundColor = allTargetsMet ? config.completionColor() : config.backgroundColor();
        panelComponent.setBackgroundColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), 150));

        // Set text color based on dark mode setting
        Color textColor = config.darkModeText() ? Color.WHITE : Color.BLACK;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Barrows Crypt Counter")
                .leftColor(textColor)
                .build());

        // Add NPC kill counts to the panel, including crypt creatures
        for (String npcName : npcList) {
            int kills = killCounts.getOrDefault(npcName, 0);
            int target = plugin.getTargetCountForNpc(npcName);
            String lineText = npcName + ": " + kills + "/" + target;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(lineText)
                    .leftColor(textColor)
                    .build());
        }

        // Add Barrows brothers kill counts to the panel if not hidden
        if (!config.hideBrothers()) {
            List<String> brothers = Arrays.asList("Ahrim", "Dharok", "Guthan", "Karil", "Torag", "Verac");
            for (String brother : brothers) {
                int kills = killCounts.getOrDefault(brother, 0);
                int target = plugin.getTargetCountForNpc(brother);
                if (target == 0) {
                    target = 1; // Default to 1 if not set
                }
                String lineText = brother + ": " + kills + "/" + target;
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(lineText)
                        .leftColor(textColor)
                        .build());
            }
        }

        // Display reward potential if enabled
        if (config.showRewardPotential()) {
            final int rewardPotential = client.getVarbitValue(Varbits.BARROWS_REWARD_POTENTIAL);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Potential")
                    .right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
                    .leftColor(textColor)
                    .rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
                    .build());
        }

        return panelComponent.render(graphics);
    }
}
