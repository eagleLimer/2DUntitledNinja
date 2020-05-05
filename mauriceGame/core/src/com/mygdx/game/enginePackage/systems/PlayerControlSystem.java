package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.game.KeyboardController;

public class PlayerControlSystem extends IteratingSystem {
    //todo:Move these variables to the correct place, if they shouldn't be here. I dunno man  ||
    // Might be ok to keep them here, otherwise they should be moved to a component
    private static final float DEACCELERATION = 0.1f;
    private static final float ACCELERATION = 0.5f;

    private static final float GROUND_Y_CAP = 0.6f;
    private static final float AIRBORNE_CONTROL = 0.1f;
    private static final float STAND_STILL_CAP = 0.4f;
    private static final float FALLING_MIN = -1f;//-0.3

    private int stateChanged = 0;
    private ComponentMapper<PlayerComponent> pm;
    private ComponentMapper<BodyComponent> bodm;
    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<VelocityComponent> vm;
    private KeyboardController controller;

    @SuppressWarnings("unchecked")
    public PlayerControlSystem(KeyboardController controller) {
        super(Family.all(PlayerComponent.class).get());
        this.controller = controller;
        pm = ComponentMapper.getFor(PlayerComponent.class);
        bodm = ComponentMapper.getFor(BodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
        vm = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        BodyComponent b2body = bodm.get(entity);
        StateComponent state = sm.get(entity);
        VelocityComponent velocity = vm.get(entity);
        stateChanged = state.get();

        if(state.grounded){
            stateChanged = StateComponent.STATE_NORMAL;
        }
        //ugly as crap? Maybe.
        if ((state.get() == StateComponent.STATE_JUMPING && b2body.body.getLinearVelocity().y < 0.00000001) || b2body.body.getLinearVelocity().y < FALLING_MIN) {
            stateChanged = StateComponent.STATE_FALLING;
            state.grounded = false;
        } /*else if (state.get() != StateComponent.STATE_JUMPING) {
        stateChanged = StateComponent.STATE_NORMAL;
        //state.grounded = false;
    }*/


        boolean airborne = stateChanged == StateComponent.STATE_FALLING || stateChanged == StateComponent.STATE_JUMPING;
        airborne = !state.grounded;
        float accMultiplier = airborne ? AIRBORNE_CONTROL : 1;
        if (controller.left) {
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -velocity.sprintSpeed, ACCELERATION * accMultiplier), b2body.body.getLinearVelocity().y);
            if (!airborne) {
                stateChanged = StateComponent.STATE_LEFT;
            }
        } else if (controller.right) {
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, velocity.sprintSpeed, ACCELERATION * accMultiplier), b2body.body.getLinearVelocity().y);
            if (!airborne) {
                stateChanged = StateComponent.STATE_RIGHT;
            }
        } else {
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 0, DEACCELERATION * accMultiplier), b2body.body.getLinearVelocity().y);
        }


        velocity.jumpCountDown -= deltaTime;
        if (controller.jump && !airborne && velocity.jumpCountDown < 0 /*&&
                (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_LEFT || state.get() == StateComponent.STATE_RIGHT)*/) {
            b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, velocity.jumpSpeed);
            //b2body.body.applyLinearImpulse(0, velocity.jumpForce, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
            stateChanged = StateComponent.STATE_JUMPING;
            state.grounded = false;
            velocity.jumpCountDown = velocity.jumpCooldown;
        }
        if (state.get() != stateChanged) {
            state.set(stateChanged);
        }

    }
}
