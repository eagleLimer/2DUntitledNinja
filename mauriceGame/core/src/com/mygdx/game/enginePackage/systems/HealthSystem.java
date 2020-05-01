package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.HealthComponent;

public class HealthSystem extends IteratingSystem {
    ComponentMapper<HealthComponent> healthM;
    public HealthSystem() {
        super(Family.all(HealthComponent.class).get());
        healthM = ComponentMapper.getFor(HealthComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = healthM.get(entity);
        //todo: add kill if health <= 0
        if(healthComponent.health < healthComponent.maxHealth){
            healthComponent.health = healthComponent.health + healthComponent.healthReg*deltaTime;
            if(!healthComponent.alwaysHidden) {
                healthComponent.hidden = false;
            }
        }else{
            healthComponent.hidden = true;
        }
    }
}
