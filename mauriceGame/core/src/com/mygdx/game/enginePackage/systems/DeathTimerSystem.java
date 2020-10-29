package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.BasicComponents.DeathTimerComponent;

public class DeathTimerSystem extends IteratingSystem {
    private ComponentMapper<DeathTimerComponent> deathM;
    private Array<Entity> toBeRemoved;
    public DeathTimerSystem(Array<Entity> toBeRemoved) {
        super(Family.all(DeathTimerComponent.class).get());
        deathM = ComponentMapper.getFor(DeathTimerComponent.class);
        this.toBeRemoved = toBeRemoved;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeathTimerComponent deathComponent = deathM.get(entity);
        deathComponent.deathTimer -= deltaTime;
        if(deathComponent.deathTimer <= 0){
            toBeRemoved.add(entity);
        }
    }
}
