package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.enginePackage.components.*;

public class BasicEnemyMovement extends IteratingSystem {
    private static final float ACCELERATION = 0.2f;
    private ComponentMapper<BodyComponent> bodm;
    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<PositionComponent> pm;
    private Entity player;

    public BasicEnemyMovement(Entity player) {
        super(Family.all(BasicEnemyComponent.class, ActivatedComponent.class).get());
        bodm = ComponentMapper.getFor(BodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
        pm = ComponentMapper.getFor(PositionComponent.class);
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (pm.get(player) != null) {
            Vector3 playerPos = pm.get(player).position;
            Vector3 enemyPos = pm.get(entity).position;
            BodyComponent body = bodm.get(entity);
            VelocityComponent velocity = vm.get(entity);

            if (playerPos.x < enemyPos.x) {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, -velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            } else {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            }
        }
    }
}
