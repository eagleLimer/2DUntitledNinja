package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.*;

import static com.mygdx.game.enginePackage.Constants.RENDERUNITS_PER_METER;
import static com.mygdx.game.game.MyGdxGame.worldWidth;

public class DeactivationSystem extends IteratingSystem {
    public static final float ACTIVATION_DISTANCE = (worldWidth)/ RENDERUNITS_PER_METER;
    public static final float SCENERY_MULTIPLIER = 1.2f;
    private Entity player;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<CollisionTypeComponent> collisionM;
    public DeactivationSystem(Entity player) {
        super(Family.all(ActivatedComponent.class).get());
        this.player = player;
        posM = ComponentMapper.getFor(PositionComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        collisionM = ComponentMapper.getFor(CollisionTypeComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(bodyM.get(entity) != null) {
            float activeDistance = collisionM.get(entity).type == CollisionTypeComponent.SCENERY ? SCENERY_MULTIPLIER * ACTIVATION_DISTANCE : ACTIVATION_DISTANCE;
            if (Math.abs(posM.get(player).position.x - posM.get(entity).position.x) > activeDistance &&
                    Math.abs(posM.get(player).position.y - posM.get(entity).position.y) > activeDistance) {
                entity.remove(ActivatedComponent.class);
                bodyM.get(entity).body.setActive(false);
                entity.add(new DeactivatedComponent());
            }
        }
    }
}
