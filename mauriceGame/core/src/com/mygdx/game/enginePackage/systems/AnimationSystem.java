package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.AnimationComponent;
import com.mygdx.game.enginePackage.components.StateComponent;
import com.mygdx.game.enginePackage.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    ComponentMapper<TextureComponent> tm;
    ComponentMapper<AnimationComponent> am;
    ComponentMapper<StateComponent> sm;

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class, ActivatedComponent.class).get());

        tm = ComponentMapper.getFor(TextureComponent.class);
        am = ComponentMapper.getFor(AnimationComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        AnimationComponent animationComponent = am.get(entity);
        StateComponent stateComponent = sm.get(entity);
        if (animationComponent.animationMap.containsKey(stateComponent.get())) {
            TextureComponent textureComponent = tm.get(entity);
            textureComponent.region = (TextureRegion) animationComponent.animationMap.get(stateComponent.get()).getKeyFrame(stateComponent.time, stateComponent.isLooping);
        }
        stateComponent.time += deltaTime;
    }
}
