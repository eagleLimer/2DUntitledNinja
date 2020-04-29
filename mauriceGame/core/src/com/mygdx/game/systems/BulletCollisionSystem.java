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
    ComponentMapper<TypeComponent> typeM;

    private Array<Entity> toBeRemoved;

    public BulletCollisionSystem(Array<Entity> toBeRemoved) {
        super(Family.all(BulletComponent.class).get());
        this.toBeRemoved = toBeRemoved;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        bulletM = ComponentMapper.getFor(BulletComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        typeM = ComponentMapper.getFor(TypeComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bulletM.get(entity).bulletTimer -= deltaTime;
        CollisionComponent cc = collisionM.get(entity);
        for (Entity collidingEntity: cc.collidingEntities) {
            if (collidingEntity != null) {
                TypeComponent type = typeM.get(collidingEntity);
                if (type != null) {
                    System.out.println("bullet hit type: " + type.type);
                    switch (type.type) {
                        case TypeComponent.BASIC_ENEMY:
                            System.out.println("bullet hit enemy");
                            DamageComponent damageComponent = damageM.get(entity);
                            if(damageComponent.damageTimer <= 0) {
                                healthM.get(collidingEntity).health -= damageM.get(entity).damage;
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
                }
            }
        }
        if(bodyM.get(entity).body.getLinearVelocity().len() < bulletM.get(entity).bulletMinSpeed && cc.collidingEntities.size >= 0) {
            toBeRemoved.add(entity);
        }
    }
}
