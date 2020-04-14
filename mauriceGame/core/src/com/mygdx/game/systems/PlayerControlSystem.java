package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.components.BodyComponent;
import com.mygdx.game.components.PlayerComponent;
import com.mygdx.game.components.StateComponent;
import com.mygdx.game.components.VelocityComponent;
import com.mygdx.game.game.KeyboardController;

public class PlayerControlSystem extends IteratingSystem {
    //todo:Move these variables to the correct place, if they shouldn't be here. I dunno man
    private static final float GROUND_Y_CAP = 0.5f;
    private static final float STAND_STILL_CAP = 0.4f;
    private static final float FALLING_CAP = -1f;

    private int stateChanged = 0;
    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<BodyComponent> bodm;
    ComponentMapper<StateComponent> sm;
    ComponentMapper<VelocityComponent> vm;
    KeyboardController controller;


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

        //OLD MOVEMENT SYSTEM

        /*
        if(b2body.body.getLinearVelocity().y < FALLING_CAP){
            state.set(StateComponent.STATE_FALLING);
        }

        if(Math.abs(b2body.body.getLinearVelocity().y)<GROUND_Y_CAP){
            if(state.get() == StateComponent.STATE_FALLING){
                state.set(StateComponent.STATE_NORMAL);
            }
            if(Math.abs(b2body.body.getLinearVelocity().x) > STAND_STILL_CAP && state.get() == StateComponent.STATE_NORMAL){
                state.set(StateComponent.STATE_MOVING);
            }
        }
        */



        if(controller.left){
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -velocity.sprintSpeed, 0.2f),b2body.body.getLinearVelocity().y);
            if(state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_RIGHT) {
                stateChanged = StateComponent.STATE_LEFT;
            }
        }
        if(controller.right){
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, velocity.sprintSpeed, 0.2f),b2body.body.getLinearVelocity().y);
            if(state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_LEFT){
                stateChanged = StateComponent.STATE_RIGHT;
            }
        }

        if(!controller.left && ! controller.right){
            b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 0, 0.1f),b2body.body.getLinearVelocity().y);
        }

        if(b2body.body.getLinearVelocity().y < 0){
            stateChanged = StateComponent.STATE_FALLING;
        }
        if(Math.abs(b2body.body.getLinearVelocity().y)<GROUND_Y_CAP){
            if(state.get() == StateComponent.STATE_FALLING ){
                stateChanged = StateComponent.STATE_NORMAL;
            }
        }

        velocity.jumpCountDown -= deltaTime;
        if(controller.jump && velocity.canJump && velocity.jumpCountDown < 0 &&
                (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_LEFT || state.get() == StateComponent.STATE_RIGHT)){
            b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, velocity.jumpSpeed);
            //b2body.body.applyLinearImpulse(0, velocity.jumpForce, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
            stateChanged = StateComponent.STATE_JUMPING;
            velocity.canJump = false;
            velocity.jumpCountDown = velocity.jumpCooldown;
        }
        if(state.get()!= stateChanged){
            state.set(stateChanged);
        }
    }
}
