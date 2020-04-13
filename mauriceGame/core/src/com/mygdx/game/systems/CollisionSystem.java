package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mygdx.game.components.*;
import com.mygdx.game.game.Map;

public class CollisionSystem  extends IteratingSystem {
    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<StateComponent> sm;
    private Map map;

    @SuppressWarnings("unchecked")
    public CollisionSystem(Map map) {
        // only player collision for now
        super(Family.all(CollisionComponent.class,PlayerComponent.class).get());
        this.map = map;
        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = cm.get(entity);

        Entity collidedEntity = cc.collisionEntity;
        if(collidedEntity != null){
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            if(type != null){
                switch(type.type){
                    case TypeComponent.ENEMY:
                        System.out.println("player hit enemy");
                        break;
                    case TypeComponent.SCENERY:
                        int state = sm.get(entity).get();
                        if(state != StateComponent.STATE_JUMPING) {
                            vm.get(entity).canJump = true;
                        }
                        System.out.println("player hit scenery");
                        break;
                    case TypeComponent.OTHER:
                        System.out.println("player hit other");
                        break;
                }
                cc.collisionEntity = null; // collision handled reset component
            }
        }

    }

}
