package net.runelite.client.plugins.ezolm.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IconSize
{
    DEFAULT("Default"),
    MEDIUM("Medium"),
    HUGE("Huge");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
