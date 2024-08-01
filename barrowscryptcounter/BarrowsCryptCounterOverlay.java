package net.runelite.client.plugins.barrowscryptcounter;

import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class BarrowsCryptCounterOverlay extends OverlayPanel {
    private final Client client;
    private final BarrowsCryptCounterPlugin plugin;
    private final BarrowsCryptCounterConfig config;

    private Map<String, Integer> killCounts;
    private List<String> npcList;

    @Inject
    private BarrowsCryptCounterOverlay(BarrowsCryptCounterPlugin plugin, Client client, BarrowsCryptCounterConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Barrows Counter Overlay");
    }

    public void setKillCounts(Map<String, Integer> killCounts) {
        this.killCounts = killCounts;
    }

    public void setNpcList(List<String> npcList) {
        this.npcList = npcList;
    }

    private void drawNPC(Graphics2D graphics, String npcName, int x, int y) {
        int kills = killCounts.getOrDefault(npcName, 0);
        int targetKills;

        switch (npcName) {
            case "Crypt Rat":
                targetKills = config.targetCryptRat();
                break;
            case "Bloodworm":
                targetKills = config.targetBloodworm();
                break;
            case "Crypt Spider":
                targetKills = config.targetCryptSpider();
                break;
            case "Giant Crypt Rat":
                targetKills = config.targetGiantCryptRat();
                break;
            case "Skeleton":
                targetKills = config.targetSkeleton();
                break;
            case "Giant Crypt Spider":
                targetKills = config.targetGiantCryptSpider();
                break;
            default:
                targetKills = 0;
                break;
        }

        if (kills >= targetKills) {
            graphics.setColor(Color.GREEN);
        } else {
            graphics.setColor(Color.WHITE);
        }

        graphics.drawString(npcName + ": " + kills + "/" + targetKills, x, y);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (npcList == null || killCounts == null) {
            return null; // Prevent rendering if data is not initialized
        }

        int panelHeight = 30 + npcList.size() * 20;
        graphics.setColor(new Color(0, 0, 0, 150));
        graphics.fillRect(10, 10, 150, panelHeight);

        int y = 30;
        graphics.setFont(FontManager.getRunescapeSmallFont());

        graphics.setColor(Color.WHITE);
        graphics.drawString("Barrows Kill Count", 15, y);
        y += 20;

        for (String npcName : npcList) {
            drawNPC(graphics, npcName, 15, y);
            y += 20;
        }

        return super.render(graphics);
    }
}
