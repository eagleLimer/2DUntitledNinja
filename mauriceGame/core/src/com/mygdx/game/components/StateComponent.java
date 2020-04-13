package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_JUMPING = 1;
    public static final int STATE_FALLING = 2;
    public static final int STATE_MOVING = 3;
    public static final int STATE_HIT = 4;

    private int state = 0;
    public float time = 0.0f;
    public boolean isLooping = false;

    public void set(int newState){
        state = newState;
        time = 0.0f;
    }

    public int get(){
        return state;
    }
    public String getByID(int id){
        switch (id){
            case STATE_NORMAL:
                return "STATE_NORMAL";
            case STATE_FALLING:
                return "STATE FALLING";
            case STATE_JUMPING:
                return "STATE JUMPING";
            case STATE_MOVING:
                return "STATE MOVING";
            case STATE_HIT:
                return "STATE HIT";
        }
        return "unknown state";
    }
}
