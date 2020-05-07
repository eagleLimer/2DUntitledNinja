package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.enginePackage.components.*;

//todo: change this! world query is really inefficient, would be much better to just make another sensorBody over player that sucks the coins.
public class CollectorSystem extends IteratingSystem {
    private static final float PULL_STRENGTH = 15;
    private static final float FOLLOW_SPEED = 10;
    private ComponentMapper<CollectorComponent> collectorM;
    private ComponentMapper<ItemComponent> itemM;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<CollisionComponent> collisionM;

    public CollectorSystem() {
        super(Family.all(CollectorComponent.class, ActivatedComponent.class).get());
        collectorM = ComponentMapper.getFor(CollectorComponent.class);
        itemM = ComponentMapper.getFor(ItemComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        collisionM = ComponentMapper.getFor(CollisionComponent.class);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Entity parent = collectorM.get(entity).parent;
        Vector3 parentPos = posM.get(parent).position;

        CollisionComponent collisionComponent = collisionM.get(entity);
        for (Entity item: collisionComponent.collidingEntities) {
            if(itemM.get(item) != null){
                Vector3 itemPos = posM.get(item).position;
                Vector2 distance = new Vector2(parentPos.x - itemPos.x,parentPos.y - itemPos.y);
                BodyComponent bodyComponent = bodyM.get(item);
                float pullForce = PULL_STRENGTH/distance.len(); // pull stronger when item closer
                bodyComponent.body.setLinearVelocity(distance.nor().scl(pullForce));
            }else{
            }
        }
        Vector3 collectorPos = posM.get(entity).position;

        Vector2 dir = new Vector2(parentPos.x - collectorPos.x, parentPos.y-collectorPos.y);
        bodyM.get(entity).body.setLinearVelocity(dir.nor().scl(FOLLOW_SPEED));
    }
}
