package net.runelite.client.plugins.barrowscryptcounter;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("barrowscryptcounter")
public interface BarrowsCryptCounterConfig extends Config
{
    @ConfigSection(
            name = "Crypt Crypt Counter",
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
            name = "Panel",
            description = "Settings for the display panel",
            position = 1
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
            keyName = "completionColor",
            name = "Completion Color",
            description = "Set the background color when all targets are met",
            position = 3,
            section = panelSection
    )
    default Color completionColor() {
        return new Color(0, 255, 0, 150);
    }

    // Add config items for each Barrows brother
    @ConfigItem(
            keyName = "targetAhrim",
            name = "Target number for Ahrim",
            description = "Set the target number for Ahrim kills",
            position = 6,
            section = cryptCounterSection
    )
    default int targetAhrim() {
        return 1;
    }

    @ConfigItem(
            keyName = "targetDharok",
            name = "Target number for Dharok",
            description = "Set the target number for Dharok kills",
            position = 7,
            section = cryptCounterSection
    )
    default int targetDharok() {
        return 1;
    }

    @ConfigItem(
            keyName = "targetGuthan",
            name = "Target number for Guthan",
            description = "Set the target number for Guthan kills",
            position = 8,
            section = cryptCounterSection
    )
    default int targetGuthan() {
        return 1;
    }

    @ConfigItem(
            keyName = "targetKaril",
            name = "Target number for Karil",
            description = "Set the target number for Karil kills",
            position = 9,
            section = cryptCounterSection
    )
    default int targetKaril() {
        return 1;
    }

    @ConfigItem(
            keyName = "targetTorag",
            name = "Target number for Torag",
            description = "Set the target number for Torag kills",
            position = 10,
            section = cryptCounterSection
    )
    default int targetTorag() {
        return 1;
    }

    @ConfigItem(
            keyName = "targetVerac",
            name = "Target number for Verac",
            description = "Set the target number for Verac kills",
            position = 11,
            section = cryptCounterSection
    )
    default int targetVerac() {
        return 1;
    }

    @ConfigItem(
            keyName = "hideBrothers",
            name = "Hide Brothers",
            description = "Toggle to hide Barrows brothers in the overlay"
    )
    default boolean hideBrothers() {
        return false; // Default to false if not explicitly set
    }

}

