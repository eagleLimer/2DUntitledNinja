package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.game.KeyboardController;
import com.mygdx.game.game.LevelManager;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionM;
    private ComponentMapper<CollisionTypeComponent> typeM;
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<DamageComponent> damageM;
    private ComponentMapper<LevelSensorComponent> levelM;
    private LevelManager levelManager;
    private KeyboardController controller;


    @SuppressWarnings("unchecked")
    public PlayerCollisionSystem(LevelManager levelManager, KeyboardController controller) {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
        this.levelManager = levelManager;
        this.controller = controller;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        typeM = ComponentMapper.getFor(CollisionTypeComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        levelM = ComponentMapper.getFor(LevelSensorComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = collisionM.get(entity);
        for (Entity collidingEntity: cc.collidingEntities) {
            if (collidingEntity != null) {
                CollisionTypeComponent type = typeM.get(collidingEntity);
                if (type != null) {

                    switch (type.type) {
                        case CollisionTypeComponent.ENEMY:
                            DamageComponent damageComponent = damageM.get(collidingEntity);
                            if (damageComponent.damageTimer <= 0) {
                                healthM.get(entity).health -= damageComponent.damage;
                                damageComponent.damageTimer = damageComponent.damageCD;
                            }
                            //System.out.println("player hit enemy");
                            break;
                        case CollisionTypeComponent.SCENERY:
                            //System.out.println("player hit scenery");
                            break;
                        case CollisionTypeComponent.OTHER:
                            //hm.get(collidedEntity).health -= 50;
                            System.out.println("player hit other");
                            break;
                        case CollisionTypeComponent.LEVEL_PORTAL:
                            if(controller.up) {
                                levelManager.loadLevel(levelM.get(collidingEntity).nextLevelName);
                            }
                            break;
                    }
                    //todo: fix: damage isn't dealt every time enemy can deal damage because collision is reset but they are still colliding.
                }
            }
        }

    }

}
