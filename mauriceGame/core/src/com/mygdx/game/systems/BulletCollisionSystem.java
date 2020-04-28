package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.*;

public class BulletCollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> collisionM;
    ComponentMapper<HealthComponent> healthM;
    ComponentMapper<DamageComponent> damageM;
    ComponentMapper<BulletComponent> bulletM;
    ComponentMapper<BodyComponent> bodyM;

    private Array<Entity> toBeRemoved;

    public BulletCollisionSystem(Array<Entity> toBeRemoved) {
        super(Family.all(BulletComponent.class).get());
        this.toBeRemoved = toBeRemoved;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        bulletM = ComponentMapper.getFor(BulletComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bulletM.get(entity).bulletTimer -= deltaTime;

        CollisionComponent cc = collisionM.get(entity);
        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            if (type != null) {
                switch (type.type) {
                    case TypeComponent.BASIC_ENEMY:
                        DamageComponent damageComponent = damageM.get(entity);
                        if(damageComponent.damageTimer <= 0) {
                            healthM.get(collidedEntity).health -= damageM.get(entity).damage;
                            damageComponent.damageTimer = damageComponent.damageCD;
                        }
                        //hm.get(collidedEntity).health -= 10;
                        break;
                    case TypeComponent.SCENERY:
                        break;
                    case TypeComponent.OTHER:
                        //hm.get(collidedEntity).health -= 50;
                        break;
                }
                cc.collisionEntity = null; // collision handled reset component
            }
            if(bodyM.get(entity).body.getLinearVelocity().len() < bulletM.get(entity).bulletMinSpeed){
                toBeRemoved.add(entity);
            }
        }
    }
}
