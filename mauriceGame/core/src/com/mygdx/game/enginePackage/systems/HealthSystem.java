package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.BasicComponents.ActivatedComponent;
import com.mygdx.game.enginePackage.components.combatComponents.HealthBarComponent;
import com.mygdx.game.enginePackage.components.combatComponents.HealthComponent;

public class HealthSystem extends IteratingSystem {
    private ComponentMapper<HealthComponent> healthM;
    private ComponentMapper<HealthBarComponent> healthBarM;
    private Array<Entity> toBeRemoved;
    public HealthSystem(Array<Entity> toBeRemoved) {
        super(Family.all(HealthComponent.class, ActivatedComponent.class).get());
        this.toBeRemoved = toBeRemoved;
        healthM = ComponentMapper.getFor(HealthComponent.class);
        healthBarM = ComponentMapper.getFor(HealthBarComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = healthM.get(entity);
        if (healthM.get(entity).health <= 0) {
            toBeRemoved.add(entity);
        }
        //todo: kill if enemy outside mapBounds, also add mapBounds
        //todo: fix this mess, dont want everyone with health to need healthbar, also dont want to check a lot
        HealthBarComponent barComponent = healthBarM.get(entity);
        if(healthComponent.health < healthComponent.maxHealth) {
            healthComponent.health = healthComponent.health + healthComponent.healthReg * deltaTime;
            if (barComponent != null) {
                if (!barComponent.alwaysHidden) {
                    barComponent.hidden = false;
                }
            }
        } else if(!barComponent.alwaysHidden){
            barComponent.hidden = true;
        }
    }
}
