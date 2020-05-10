package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.enginePackage.components.*;

import static com.mygdx.game.enginePackage.systems.DeactivationSystem.ACTIVATION_DISTANCE;
import static com.mygdx.game.enginePackage.systems.DeactivationSystem.SCENERY_MULTIPLIER;

//NOTE: MIGHT ACTUALLY ONLY NEED ONE SYSTEM NOT ACTIVATION-/DEACTIVATION-SYSTEM
public class ActivationSystem extends IteratingSystem {
    private Entity player;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<CollisionTypeComponent> collisionM;

    public ActivationSystem(Entity player) {
        super(Family.all(DeactivatedComponent.class).get());
        //super(Family.exclude(ActivatedComponent.class).get()); //TRY THIS SOMETIME.
        this.player = player;
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        collisionM = ComponentMapper.getFor(CollisionTypeComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        float activeDistance = collisionM.get(entity).type == CollisionTypeComponent.SCENERY ? SCENERY_MULTIPLIER*ACTIVATION_DISTANCE : ACTIVATION_DISTANCE;
        if (Math.abs(posM.get(player).position.x - posM.get(entity).position.x) < activeDistance &&
                Math.abs(posM.get(player).position.y - posM.get(entity).position.y) < activeDistance) {
            //maybe give this class creator and keep body information as a new class that keeps all important information.
            bodyM.get(entity).body.setActive(true);
            entity.remove(DeactivatedComponent.class);
            entity.add(new ActivatedComponent());
        }
    }
}
