package com.mygdx.game.enginePackage.systems.enemySystems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.BodyComponent;
import com.mygdx.game.enginePackage.components.PositionComponent;
import com.mygdx.game.enginePackage.components.VelocityComponent;
import com.mygdx.game.enginePackage.components.enemyComponents.AggressiveComponent;
import com.mygdx.game.enginePackage.components.enemyComponents.GhostMovementComponent;

public class GhostMovementSystem extends IteratingSystem {
    private static final float ACCELERATION = 0.2f;
    private Entity player;
    private final ComponentMapper<BodyComponent> bodyM;
    private final ComponentMapper<VelocityComponent> velocityM;
    private final ComponentMapper<PositionComponent> positionM;
    private final ComponentMapper<AggressiveComponent> aggroM;
    private final ComponentMapper<GhostMovementComponent> ghostM;

    public GhostMovementSystem(Entity player) {
        super(Family.all(GhostMovementComponent.class,ActivatedComponent.class).get());
        this.player = player;
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        velocityM = ComponentMapper.getFor(VelocityComponent.class);
        positionM = ComponentMapper.getFor(PositionComponent.class);
        aggroM = ComponentMapper.getFor(AggressiveComponent.class);
        ghostM = ComponentMapper.getFor(GhostMovementComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(aggroM.get(entity).aggressive && positionM.get(player) != null){
            Vector3 playerPos = positionM.get(player).position;
            Vector3 enemyPos = positionM.get(entity).position;
            BodyComponent body = bodyM.get(entity);
            VelocityComponent velocity = velocityM.get(entity);

            Vector2 difference = new Vector2(playerPos.x,playerPos.y).sub(enemyPos.x,enemyPos.y);
            if(difference.len() > ghostM.get(entity).preferredDistance){
                body.body.setLinearVelocity(difference.nor().scl(velocity.sprintSpeed));
            }
        }
    }
}
