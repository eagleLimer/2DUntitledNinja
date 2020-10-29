package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class CollectorComponent implements Component {
    public Entity parent;
    public float eatRange;

    public CollectorComponent(Entity parent, float eatRange) {
        this.parent = parent;
        this.eatRange = eatRange;
    }
}
