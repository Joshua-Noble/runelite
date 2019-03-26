package net.runelite.client.plugins.ezolm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.ezolm.config.IconSize;

@ConfigGroup("ezolm")
public interface EzOlmConfig extends Config
{
    @ConfigItem(
            keyName = "iconSize",
            name = "Icon Size",
            description = "Choose prayer icon size",
            position = 1
    )
    default IconSize iconSize()
    {
        return IconSize.DEFAULT;
    }
}
