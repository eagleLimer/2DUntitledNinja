package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.playerComponents.LevelComponent;

public class LevelSystem extends IteratingSystem {
    private ComponentMapper<LevelComponent> levelM;
    public LevelSystem() {
        super(Family.all(LevelComponent.class).get());
        levelM = ComponentMapper.getFor(LevelComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        LevelComponent levelComponent = levelM.get(entity);
        if(levelComponent.progress > levelComponent.required){
            levelComponent.level +=1;
        }
    }
}
