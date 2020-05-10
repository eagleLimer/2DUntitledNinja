package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.CollisionBooleans;
import com.mygdx.game.enginePackage.components.CollisionComponent;

public class CollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> collisionM;
    public CollisionSystem() {
        super(Family.all(CollisionComponent.class,ActivatedComponent.class).get());
            collisionM = ComponentMapper.getFor(CollisionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionComponent = collisionM.get(entity);
        Array<Entity> removeCollision = new Array<>();
        for (CollisionBooleans booleans:collisionComponent.collidingEntities.values()) {
            if(!booleans.processed){
                booleans.processed = true;
            }else if(booleans.shouldRemove || booleans.collidedEntity == null){
                removeCollision.add(booleans.collidedEntity);
            }
        }
        for (Entity removeEntity:removeCollision) {
            collisionComponent.collidingEntities.remove(removeEntity);
        }
        removeCollision.clear();
    }
}
