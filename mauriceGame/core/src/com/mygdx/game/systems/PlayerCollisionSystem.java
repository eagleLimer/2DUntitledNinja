package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.components.*;
import com.mygdx.game.game.LevelManager;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionM;
    private ComponentMapper<TypeComponent> typeM;
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<DamageComponent> damageM;
    private ComponentMapper<LevelSensorComponent> levelM;
    private LevelManager levelManager;


    @SuppressWarnings("unchecked")
    public PlayerCollisionSystem(LevelManager levelManager) {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
        this.levelManager = levelManager;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        typeM = ComponentMapper.getFor(TypeComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        levelM = ComponentMapper.getFor(LevelSensorComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = collisionM.get(entity);
        for (Entity collidingEntity: cc.collidingEntities) {
            if (collidingEntity != null) {
                TypeComponent type = typeM.get(collidingEntity);
                if (type != null) {
                    if (type.type == TypeComponent.BULLET) {
                        //healthM.get(entity).health -= damageM.get(collidedEntity).damage;
                    }
                    switch (type.type) {
                        case TypeComponent.BASIC_ENEMY:
                            DamageComponent damageComponent = damageM.get(collidingEntity);
                            if (damageComponent.damageTimer <= 0) {
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
                        case TypeComponent.LEVEL_SENSOR:
                            levelManager.loadLevel(levelM.get(collidingEntity).nextLevelName);
                            break;
                    }
                    //todo: fix: damage isn't dealt every time enemy can deal damage because collision is reset but they are still colliding.
                }
            }
        }

    }

}
