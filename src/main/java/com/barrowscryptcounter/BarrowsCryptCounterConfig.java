package com.barrowscryptcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("barrowstracker")
public interface BarrowsCryptCounterConfig extends Config
{
    @ConfigItem(
            keyName = "targetCryptRat",
            name = "Target number for Crypt Rat",
            description = "Set the target number for Crypt Rat kills"
    )
    default int targetCryptRat() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetBloodworm",
            name = "Target number for Bloodworm",
            description = "Set the target number for Bloodworm kills"
    )
    default int targetBloodworm() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetCryptSpider",
            name = "Target number for Crypt Spider",
            description = "Set the target number for Crypt Spider kills"
    )
    default int targetCryptSpider() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetGiantCryptRat",
            name = "Target number for Giant Crypt Rat",
            description = "Set the target number for Giant Crypt Rat kills"
    )
    default int targetGiantCryptRat() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetSkeleton",
            name = "Target number for Skeleton",
            description = "Set the target number for Skeleton kills"
    )
    default int targetSkeleton() {
        return 0;
    }

    @ConfigItem(
            keyName = "targetGiantCryptSpider",
            name = "Target number for Giant Crypt Spider",
            description = "Set the target number for Giant Crypt Spider kills"
    )
    default int targetGiantCryptSpider() {
        return 0;
    }
}
