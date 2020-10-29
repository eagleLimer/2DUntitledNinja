package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;

public class DeathTimerComponent implements Component {
    public float deathTimer;

    public DeathTimerComponent(float deathTimer) {
        this.deathTimer = deathTimer;
    }
}
