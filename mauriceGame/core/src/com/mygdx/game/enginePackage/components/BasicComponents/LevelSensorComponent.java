package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;

public class LevelSensorComponent implements Component {
    public LevelSensorComponent(String nextLevelName) {
        this.nextLevelName = nextLevelName;
    }

    public String nextLevelName;
}
