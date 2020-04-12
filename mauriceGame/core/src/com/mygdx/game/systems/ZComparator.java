package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.components.PositionComponent;

import java.util.Comparator;
public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<PositionComponent> cmTrans;

    public ZComparator(){
        cmTrans= ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public int compare(Entity entityA, Entity entityB) {
        float az = cmTrans.get(entityA).position.z;
        float bz = cmTrans.get(entityB).position.z;
        int res = 0;
        if(az > bz){
            res = 1;
        }else if(az < bz){
            res = -1;
        }
        return res;
    }
}
