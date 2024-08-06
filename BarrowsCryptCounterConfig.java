package net.runelite.client.plugins.barrowscryptcounter;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("barrowscryptcounter")
public interface BarrowsCryptCounterConfig extends Config
{
    @ConfigSection(
            name = "Crypt Creatures Counter",
            description = "Settings for counting crypt creatures",
            position = 0
    )
    String cryptCounterSection = "cryptCounterSection";

    @ConfigItem(
            keyName = "targetCryptRat",
            name = "Crypt Rat",
            description = "Set the target number for Crypt Rat kills",
            position = 0,
            section = cryptCounterSection
    )
    default int targetCryptRat() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetBloodworm",
            name = "Bloodworm",
            description = "Set the target number for Bloodworm kills",
            position = 1,
            section = cryptCounterSection
    )
    default int targetBloodworm() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetCryptSpider",
            name = "Crypt Spider",
            description = "Set the target number for Crypt Spider kills",
            position = 2,
            section = cryptCounterSection
    )
    default int targetCryptSpider() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetGiantCryptRat",
            name = "Giant Crypt Rat",
            description = "Set the target number for Giant Crypt Rat kills",
            position = 3,
            section = cryptCounterSection
    )
    default int targetGiantCryptRat() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetSkeleton",
            name = "Skeleton",
            description = "Set the target number for Skeleton kills",
            position = 4,
            section = cryptCounterSection
    )
    default int targetSkeleton() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetGiantCryptSpider",
            name = "Giant Crypt Spider",
            description = "Set the target number for Giant Crypt Spider kills",
            position = 5,
            section = cryptCounterSection
    )
    default int targetGiantCryptSpider() {
        return 0;
    }

    @ConfigSection(
            name = "Brothers Counter",
            description = "Settings for counting Barrows brothers",
            position = 1
    )
    String brothersCounterSection = "brothersCounterSection";

    @ConfigItem(
            keyName = "targetAhrim",
            name = "Ahrim",
            description = "Set the target number for Ahrim kills",
            position = 6,
            section = brothersCounterSection
    )
    default int targetAhrim() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetDharok",
            name = "Dharok",
            description = "Set the target number for Dharok kills",
            position = 7,
            section = brothersCounterSection
    )
    default int targetDharok() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetGuthan",
            name = "Guthan",
            description = "Set the target number for Guthan kills",
            position = 8,
            section = brothersCounterSection
    )
    default int targetGuthan() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetKaril",
            name = "Karil",
            description = "Set the target number for Karil kills",
            position = 9,
            section = brothersCounterSection
    )
    default int targetKaril() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetTorag",
            name = "Torag",
            description = "Set the target number for Torag kills",
            position = 10,
            section = brothersCounterSection
    )
    default int targetTorag() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetVerac",
            name = "Verac",
            description = "Set the target number for Verac kills",
            position = 11,
            section = brothersCounterSection
    )
    default int targetVerac() {
        return 0;
    }

    @ConfigSection(
            name = "Panel",
            description = "Settings for the display panel",
            position = 2
    )
    String panelSection = "panelSection";

    @ConfigItem(
            keyName = "showRewardPotential",
            name = "Show Reward Potential",
            description = "Toggle the display of the reward potential",
            position = 0,
            section = panelSection
    )
    default boolean showRewardPotential() {
        return true;
    }

    @ConfigItem(
            keyName = "darkModeText",
            name = "Switch Text Color",
            description = "Toggle between white text (dark mode) and black text",
            position = 1,
            section = panelSection
    )
    default boolean darkModeText() {
        return false;
    }

    @ConfigItem(
            keyName = "backgroundColor",
            name = "Background Color",
            description = "Set the background color of the panel",
            position = 2,
            section = panelSection
    )
    default Color backgroundColor() {
        return new Color(0, 0, 0, 150);
    }

    @ConfigItem(
            keyName = "completionBorderColor",
            name = "Completion Border Color",
            description = "Set the border color when all targets are met",
            position = 3,
            section = panelSection
    )
    default Color completionBorderColor() {
        return new Color(0, 255, 0, 150);
    }

    @ConfigItem(
            keyName = "resetKillCounts",
            name = "Click to reset Kill Counts",
            description = "Reset all kill counts",
            position = 5,
            section = panelSection
    )
    default Button resetKillCounts() {
        return new Button();
    }
}
