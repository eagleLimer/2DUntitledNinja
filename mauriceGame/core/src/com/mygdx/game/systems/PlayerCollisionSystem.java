package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.components.*;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionM;
    private ComponentMapper<TypeComponent> typeM;
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<DamageComponent> damageM;



    @SuppressWarnings("unchecked")
    public PlayerCollisionSystem() {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        typeM = ComponentMapper.getFor(TypeComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = collisionM.get(entity);
        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            TypeComponent type = typeM.get(collidedEntity);
            if (type != null) {
                if(type.type == TypeComponent.BULLET){
                    //healthM.get(entity).health -= damageM.get(collidedEntity).damage;
                }
                switch (type.type) {
                    case TypeComponent.BASIC_ENEMY:
                        DamageComponent damageComponent = damageM.get(collidedEntity);
                        if(damageComponent.damageTimer<= 0){
                            healthM.get(entity).health -= damageComponent.damage;
                            damageComponent.damageTimer = damageComponent.damageCD;
                        }
                        //System.out.println("player hit enemy");
                        break;
                    case TypeComponent.SCENERY:
                        //System.out.println("player hit scenery");
                        break;
                    case TypeComponent.OTHER:
                        //hm.get(collidedEntity).health -= 50;
                        System.out.println("player hit other");
                        break;
                }
                //todo: fix: damage isn't dealt every time enemy can deal damage because collision is reset but they are still colliding.
                cc.collisionEntity = null; // collision handled reset component
            }
        }

    }

}
