package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.combatComponents.DamageComponent;

public class DamageSystem extends IteratingSystem {
    ComponentMapper<DamageComponent> damageM;
    public DamageSystem() {
        super(Family.all(DamageComponent.class, ActivatedComponent.class).get());
        damageM = ComponentMapper.getFor(DamageComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DamageComponent damageComponent = damageM.get(entity);
        damageComponent.damageTimer -= deltaTime;
    }
}
