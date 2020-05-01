package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.*;

public class BulletCollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> collisionM;
    ComponentMapper<HealthComponent> healthM;
    ComponentMapper<DamageComponent> damageM;
    ComponentMapper<BulletComponent> bulletM;
    ComponentMapper<BodyComponent> bodyM;
    ComponentMapper<CollisionTypeComponent> typeM;

    private Array<Entity> toBeRemoved;

    public BulletCollisionSystem(Array<Entity> toBeRemoved) {
        super(Family.all(BulletComponent.class).get());
        this.toBeRemoved = toBeRemoved;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        bulletM = ComponentMapper.getFor(BulletComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        typeM = ComponentMapper.getFor(CollisionTypeComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //todo: upon removal of shurikens set the velocity to 0 and make them not affected by gravity.
        CollisionComponent cc = collisionM.get(entity);
        for (Entity collidingEntity: cc.collidingEntities) {
            if (collidingEntity != null) {
                CollisionTypeComponent type = typeM.get(collidingEntity);
                if(typeM.get(entity).type == CollisionTypeComponent.FRIENDLY) {
                    if (type != null) {
                        switch (type.type) {
                            case CollisionTypeComponent.ENEMY:
                                DamageComponent damageComponent = damageM.get(entity);
                                if (damageComponent.damageTimer <= 0) {
                                    healthM.get(collidingEntity).health -= damageM.get(entity).damage;
                                    damageComponent.damageTimer = damageComponent.damageCD;
                                }
                                //hm.get(collidedEntity).health -= 10;
                                break;
                            case CollisionTypeComponent.SCENERY:
                                break;
                            case CollisionTypeComponent.OTHER:
                                //hm.get(collidedEntity).health -= 50;
                                break;
                        }
                    }
                }else{
                    if (type != null) {
                        switch (type.type) {
                            case CollisionTypeComponent.FRIENDLY:
                                DamageComponent damageComponent = damageM.get(entity);
                                if (damageComponent.damageTimer <= 0) {
                                    if(healthM.get(entity) != null) {
                                        healthM.get(collidingEntity).health -= damageM.get(entity).damage;
                                        damageComponent.damageTimer = damageComponent.damageCD;
                                    }
                                }
                                //hm.get(collidedEntity).health -= 10;
                                break;
                            case CollisionTypeComponent.SCENERY:
                                break;
                            case CollisionTypeComponent.OTHER:
                                //hm.get(collidedEntity).health -= 50;
                                break;
                        }
                    }
                }
                if(bodyM.get(entity).body.getLinearVelocity().len() < bulletM.get(entity).bulletMinSpeed && cc.collidingEntities.size >= 0) {
                    toBeRemoved.add(entity);
                }
            }
        }

    }
}
