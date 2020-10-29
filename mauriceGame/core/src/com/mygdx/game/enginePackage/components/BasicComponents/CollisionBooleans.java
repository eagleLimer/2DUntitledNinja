package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Entity;

public class CollisionBooleans {
    public boolean processed = false;
    public boolean shouldRemove = false;
    public Entity collidedEntity;


    public CollisionBooleans(Entity collidedEntity) {
        this.collidedEntity = collidedEntity;
    }
}
