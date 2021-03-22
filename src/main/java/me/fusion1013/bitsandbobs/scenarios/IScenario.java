package me.fusion1013.bitsandbobs.scenarios;

import org.bukkit.Material;

public interface IScenario {
    void toggle();
    boolean getEnabled();
    Material getSlotMaterial();
    String getName();
    String getDescription();
}
