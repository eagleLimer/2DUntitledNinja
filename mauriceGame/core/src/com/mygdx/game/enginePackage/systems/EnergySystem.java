package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.playerComponents.EnergyComponent;

public class EnergySystem extends IteratingSystem {
    private ComponentMapper<EnergyComponent> energyM;
    public EnergySystem() {
        super(Family.all(EnergyComponent.class, ActivatedComponent.class).get());
        energyM = ComponentMapper.getFor(EnergyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnergyComponent energyComponent = energyM.get(entity);
        if(energyComponent.mana < energyComponent.maxMana){
            energyComponent.mana += energyComponent.manaReg*deltaTime;
        }
    }
}
