package net.runelite.client.plugins.ezolm;

import net.runelite.api.AnimationID;
import net.runelite.api.Prayer;
import net.runelite.api.ProjectileID;

public enum OlmAttack {
    MAGIC(ProjectileID.OLM_MAGIC, Prayer.PROTECT_FROM_MAGIC),
    RANGE(ProjectileID.OLM_RANGED, Prayer.PROTECT_FROM_MISSILES),
    NONE(-1, null);

    private final int animation;
    private final Prayer prayer;

    OlmAttack(int animation, Prayer prayer)
    {
        this.animation = animation;
        this.prayer = prayer;
    }

    public int getAnimation()
    {
        return animation;
    }

    public Prayer getPrayer()
    {
        return prayer;
    }
}
