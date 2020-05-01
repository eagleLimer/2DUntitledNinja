package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.enginePackage.components.BodyComponent;

public class MyEntityListener implements EntityListener {
    private World world;

    public MyEntityListener(World world) {
        this.world = world;
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(entity.getComponent(BodyComponent.class) !=null) {
            world.destroyBody(entity.getComponent(BodyComponent.class).body);
        }
        entity.removeAll();
    }
}
