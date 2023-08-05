package com.barrowscryptcounter;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class BarrowsCryptCounterPanelOverlay extends Overlay {
    private final BarrowsCryptCounterPlugin plugin;
    private final PanelComponent panelComponent;
    private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");
    private final Client client;

    @Inject
    private BarrowsCryptCounterPanelOverlay(BarrowsCryptCounterPlugin plugin, Client client) {
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.client = client;
        panelComponent = new PanelComponent();
        panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
        panelComponent.setBackgroundColor(new Color(0, 0, 0, 150));
    }

    private int rewardPotential()
    {
        // this is from [proc,barrows_overlay_reward]
        int brothers = client.getVarbitValue(Varbits.BARROWS_KILLED_AHRIM)
                + client.getVarbitValue(Varbits.BARROWS_KILLED_DHAROK)
                + client.getVarbitValue(Varbits.BARROWS_KILLED_GUTHAN)
                + client.getVarbitValue(Varbits.BARROWS_KILLED_KARIL)
                + client.getVarbitValue(Varbits.BARROWS_KILLED_TORAG)
                + client.getVarbitValue(Varbits.BARROWS_KILLED_VERAC);
        return client.getVarbitValue(Varbits.BARROWS_REWARD_POTENTIAL) + brothers * 2;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Fetch the kill count data from the plugin
        Map<String, Integer> killCounts = plugin.getKillCounts();
        List npcList = plugin.getNpcList();

        // Clear the panel before adding components to avoid duplicates
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Barrows Kill Count")
                .build());

        for (Object npcName : npcList) {
            int kills = killCounts.getOrDefault(npcName, 0);
            String lineText = npcName + ": " + kills;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(lineText)
                    .build());
        }

        final int rewardPotential = rewardPotential();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Potential")
                .right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
                .rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
                .build());

        return panelComponent.render(graphics);
    }


    // Other methods for configuration, etc. if needed
}
