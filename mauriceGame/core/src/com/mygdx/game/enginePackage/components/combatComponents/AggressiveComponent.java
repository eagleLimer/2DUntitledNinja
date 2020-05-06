package com.mygdx.game.enginePackage.components.combatComponents;

import com.badlogic.ashley.core.Component;

public class AggressiveComponent implements Component {
    public float aggressionRange = 15;
    public float sightRange = 35;
    public boolean aggressive = false;

    public AggressiveComponent(float aggressionRange, float sightRange) {
        this.aggressionRange = aggressionRange;
        this.sightRange = sightRange;
    }
    public AggressiveComponent(){

    }
}
