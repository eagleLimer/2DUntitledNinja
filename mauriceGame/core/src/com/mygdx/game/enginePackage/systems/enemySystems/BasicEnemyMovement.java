package com.mygdx.game.enginePackage.systems.enemySystems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.enginePackage.components.combatComponents.AggressiveComponent;
import com.mygdx.game.enginePackage.components.enemyComponents.BasicEnemyComponent;

public class BasicEnemyMovement extends IteratingSystem {
    private static final float ACCELERATION = 0.2f;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<VelocityComponent> velocityM;
    private ComponentMapper<PositionComponent> positionM;
    private ComponentMapper<AggressiveComponent> aggroM;
    private Entity player;

    //todo: add aggressive range
    public BasicEnemyMovement(Entity player) {
        super(Family.all(BasicEnemyComponent.class, ActivatedComponent.class).get());
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        velocityM = ComponentMapper.getFor(VelocityComponent.class);
        positionM = ComponentMapper.getFor(PositionComponent.class);
        aggroM = ComponentMapper.getFor(AggressiveComponent.class);
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (positionM.get(player) != null && aggroM.get(entity).aggressive) {
            Vector3 playerPos = positionM.get(player).position;
            Vector3 enemyPos = positionM.get(entity).position;
            BodyComponent body = bodyM.get(entity);
            VelocityComponent velocity = velocityM.get(entity);

            if (playerPos.x < enemyPos.x) {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, -velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            } else {
                body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, velocity.sprintSpeed, ACCELERATION), body.body.getLinearVelocity().y);
            }
            velocity.jumpCountDown -= deltaTime;
            if(playerPos.y- enemyPos.y >2 && velocity.jumpCountDown < 0){
                body.body.setLinearVelocity(body.body.getLinearVelocity().x, velocity.jumpSpeed);
                velocity.jumpCountDown = velocity.jumpCooldown;
            }

        }
    }
}
