package com.mygdx.game.enginePackage.systems.enemySystems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.enginePackage.components.combatComponents.AggressiveComponent;
import com.mygdx.game.enginePackage.components.BasicComponents.PositionComponent;

public class AggressiveSystem extends IteratingSystem {
    private ComponentMapper<AggressiveComponent> aggroM;
    private ComponentMapper<PositionComponent> posM;
    private Entity player;
    public AggressiveSystem(Entity player) {
        super(Family.all(AggressiveComponent.class).get());
        this.player = player;
        aggroM = ComponentMapper.getFor(AggressiveComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AggressiveComponent aggro = aggroM.get(entity);
        Vector3 playerPos = posM.get(player).position;
        Vector3 enemyPos = posM.get(entity).position;
        float distanceToPlayer = new Vector2(playerPos.x,playerPos.y).sub(enemyPos.x, enemyPos.y).len();
        if(aggro.aggressive){
            if(distanceToPlayer > aggro.sightRange){
                aggro.aggressive = false;
            }
        }else{
            if (distanceToPlayer < aggro.aggressionRange){
                aggro.aggressive = true;
            }
        }
    }
}
