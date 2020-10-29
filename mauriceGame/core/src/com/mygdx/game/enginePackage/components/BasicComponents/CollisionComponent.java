package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

public class CollisionComponent implements Component {
    public HashMap<Entity, CollisionBooleans> collidingEntities = new HashMap<>();
}
