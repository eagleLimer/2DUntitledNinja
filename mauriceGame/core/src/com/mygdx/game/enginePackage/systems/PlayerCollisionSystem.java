package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.enginePackage.components.combatComponents.DamageComponent;
import com.mygdx.game.enginePackage.components.combatComponents.HealthComponent;
import com.mygdx.game.enginePackage.components.playerComponents.PlayerComponent;
import com.mygdx.game.game.KeyboardController;
import com.mygdx.game.game.LevelManager;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionM;
    private ComponentMapper<CollisionTypeComponent> typeM;
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<DamageComponent> damageM;
    private ComponentMapper<LevelSensorComponent> levelM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<StateComponent> stateM;
    private ComponentMapper<BodyComponent> bodyM;

    private LevelManager levelManager;
    private KeyboardController controller;
    private ComponentMapper<ItemComponent> itemM;
    private ComponentMapper<InventoryComponent> inventoryM;
    private Array<Entity> toBeRemoved;


    @SuppressWarnings("unchecked")
    public PlayerCollisionSystem(LevelManager levelManager, KeyboardController controller, Array<Entity> toBeRemoved) {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
        this.levelManager = levelManager;
        this.controller = controller;
        this.toBeRemoved = toBeRemoved;
        collisionM = ComponentMapper.getFor(CollisionComponent.class);
        typeM = ComponentMapper.getFor(CollisionTypeComponent.class);
        healthM = ComponentMapper.getFor(HealthComponent.class);
        damageM = ComponentMapper.getFor(DamageComponent.class);
        levelM = ComponentMapper.getFor(LevelSensorComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        stateM = ComponentMapper.getFor(StateComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        itemM = ComponentMapper.getFor(ItemComponent.class);
        inventoryM = ComponentMapper.getFor(InventoryComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = collisionM.get(entity);
        //System.out.println("playercolliding " + cc.collidingEntities.size);
        for (Entity collidingEntity : cc.collidingEntities) {
            if (collidingEntity != null) {
                CollisionTypeComponent type = typeM.get(collidingEntity);
                if (type != null) {
                    switch (type.type) {
                        case CollisionTypeComponent.ENEMY:
                            DamageComponent damageComponent = damageM.get(collidingEntity);
                            if (damageComponent.damageTimer <= 0) {
                                healthM.get(entity).health -= damageComponent.damage;
                                damageComponent.damageTimer = damageComponent.damageCD;
                                BodyComponent playerBod = bodyM.get(entity);
                                BodyComponent enemyBod = bodyM.get(collidingEntity);

                                playerBod.body.applyLinearImpulse(new Vector2(playerBod.body.getWorldCenter().sub(enemyBod.body.getWorldCenter())).nor().scl(damageComponent.damage * 2.5f), playerBod.body.getWorldCenter(), true);
                            }
                            //System.out.println("player hit enemy");
                            break;
                        case CollisionTypeComponent.SCENERY:
                            //System.out.println(posM.get(collidingEntity).position.y + " player " + posM.get(entity).position.y);
                            if ((posM.get(collidingEntity).position.y + 1f / 2) < posM.get(entity).position.y - 0.3f) {
                                StateComponent stateComponent = stateM.get(entity);
                                if (stateComponent.get() == StateComponent.STATE_FALLING || stateComponent.get() == StateComponent.STATE_JUMPING) {
                                    //stateComponent.set(StateComponent.STATE_NORMAL);
                                    stateComponent.grounded = true;
                                    //System.out.println("landed and normal!");
                                }
                            }
                            //System.out.println("player hit scenery");
                            break;
                        case CollisionTypeComponent.ITEM:
                            ItemComponent itemComponent = itemM.get(collidingEntity);
                            InventoryComponent inventory = inventoryM.get(entity);
                            switch (itemComponent.item.getItemType()) {
                                case COIN:
                                    inventory.coins++;
                                    break;
                                default:
                                    inventory.items.add(itemComponent.item);
                            }
                            toBeRemoved.add(collidingEntity);
                            break;
                        case CollisionTypeComponent.OTHER:
                            //System.out.println("player hit other");
                            break;
                        case CollisionTypeComponent.LEVEL_PORTAL:
                            if (controller.up) {
                                levelManager.loadLevel(levelM.get(collidingEntity).nextLevelName);
                            }
                            break;
                    }
                }
            } else {
                cc.collidingEntities.removeValue(collidingEntity, false);
            }
        }
    }
}
