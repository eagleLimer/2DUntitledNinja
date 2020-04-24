package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.components.*;

public class PlayerCollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> cm;
    private ComponentMapper<PlayerComponent> pm;
    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<HealthComponent> hm;
    private ComponentMapper<TextureComponent> tm;

    @SuppressWarnings("unchecked")
    public PlayerCollisionSystem() {
        super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        hm = ComponentMapper.getFor(HealthComponent.class);
        tm = ComponentMapper.getFor(TextureComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = cm.get(entity);


        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            if (type != null) {
                switch (type.type) {
                    case TypeComponent.BASIC_ENEMY:
                        //hm.get(collidedEntity).health -= 10;
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
                cc.collisionEntity = null; // collision handled reset component
            }
        }

    }

}
