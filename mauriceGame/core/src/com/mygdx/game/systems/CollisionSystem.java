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
    private Map map;

    @SuppressWarnings("unchecked")
    public CollisionSystem(Map map) {
        // only need to worry about player collisions
        super(Family.all(CollisionComponent.class,PlayerComponent.class).get());
        this.map = map;
        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // get player collision component
        CollisionComponent cc = cm.get(entity);
        // recursively get all bodies and then for every polygon shape add every edge to a list containing the coordinates of the point,
        // in case its not a polygon shape handle it another well suited way, then check if the tile the coordinates are on is collideable.

        /*for (JointEdge jointEdge: entity.getComponent(BodyComponent.class).body.getJointList()) {

        }*/

        //for now just check the position of the entity...
        if(map.getTile((int)entity.getComponent(PositionComponent.class).position.x, (int)entity.getComponent(PositionComponent.class).position.y).isCollideable()){
            System.out.println("player hit map!!!!");
        }
        Entity collidedEntity = cc.collisionEntity;
        if(collidedEntity != null){
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            if(type != null){
                switch(type.type){
                    case TypeComponent.ENEMY:
                        //do player hit enemy thing
                        System.out.println("player hit enemy");
                        break;
                    case TypeComponent.SCENERY:
                        //do player hit scenery thing
                        System.out.println("player hit scenery");
                        break;
                    case TypeComponent.OTHER:
                        //do player hit other thing
                        System.out.println("player hit other");
                        break; //technically this isn't needed
                }
                cc.collisionEntity = null; // collision handled reset component
            }
        }

    }

}
