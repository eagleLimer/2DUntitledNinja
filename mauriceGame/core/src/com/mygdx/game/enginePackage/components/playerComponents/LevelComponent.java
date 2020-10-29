package com.mygdx.game.enginePackage.components.playerComponents;

import com.badlogic.ashley.core.Component;

public class LevelComponent implements Component {
    public int level = 0;
    public float required = 100;
    public float progress = 0;
}
