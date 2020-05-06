package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class CollectorComponent implements Component {
    public float pullDistance;
    public float collectingDistance;


    public CollectorComponent(float pullDistance, float collectingDistance) {
        this.pullDistance = pullDistance;
        this.collectingDistance = collectingDistance;
    }
}
