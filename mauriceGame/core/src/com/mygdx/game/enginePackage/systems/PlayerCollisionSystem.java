package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.game.KeyboardController;
import com.mygdx.game.game.LevelManager;
import com.mygdx.game.game.Tile;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionM;
    private ComponentMapper<CollisionTypeComponent> typeM;
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<DamageComponent> damageM;
    private ComponentMapper<LevelSensorComponent> levelM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<StateComponent> stateM;

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
        posM = ComponentMapper.getFor(PositionComponent.class);
        stateM = ComponentMapper.getFor(StateComponent.class);

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
                            /*System.out.println(posM.get(collidingEntity).position.y + " player " + posM.get(entity).position.y);
                            if((posM.get(collidingEntity).position.y+ 1f/2) < posM.get(entity).position.y-0.6f){
                                StateComponent stateComponent = stateM.get(entity);
                                if (stateComponent.get() == StateComponent.STATE_FALLING || stateComponent.get() == StateComponent.STATE_JUMPING){
                                    stateComponent.set(StateComponent.STATE_NORMAL);
                                    stateComponent.grounded = true;
                                    System.out.println("landed and normal!");
                                }
                            }*/
                            //System.out.println("player hit scenery");
                            break;
                        case CollisionTypeComponent.OTHER:
                            //System.out.println("player hit other");
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
