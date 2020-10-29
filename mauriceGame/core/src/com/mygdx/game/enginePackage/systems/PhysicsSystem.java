package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.BasicComponents.ActivatedComponent;
import com.mygdx.game.enginePackage.components.BasicComponents.BodyComponent;
import com.mygdx.game.enginePackage.components.BasicComponents.PositionComponent;

public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1 / 120f;
    private static float accumulator = 0f;

    private static final int velocityIterations = 6;
    private static final int positionIterations = 2;

    private World world;
    private Array<Entity> bodiesQueue;

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<PositionComponent> tm = ComponentMapper.getFor(PositionComponent.class);

    @SuppressWarnings("unchecked")
    public PhysicsSystem(World world) {
        super(Family.all(BodyComponent.class, PositionComponent.class, ActivatedComponent.class).get());
        this.world = world;
        this.bodiesQueue = new Array<Entity>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //not sure bout this...
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, velocityIterations, positionIterations);
            accumulator -= MAX_STEP_TIME;

            world.step(deltaTime, velocityIterations, positionIterations);
            //Entity Queue
            for (Entity entity : bodiesQueue) {
                PositionComponent tfm = tm.get(entity);
                BodyComponent bodyComponent = bm.get(entity);
                Vector2 position = bodyComponent.body.getPosition();
                tfm.position.x = position.x;
                tfm.position.y = position.y;
                tfm.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
            }
        }
        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}
