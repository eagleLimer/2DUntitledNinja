package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;

public class PlayerStateComponent implements Component {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_JUMPING = 1;
    public static final int STATE_FALLING = 2;
    public static final int STATE_RIGHT = 3;
    public static final int STATE_LEFT = 4;
    public static final int STATE_HIT = 6;
    public static final int STATE_DEAD = 7;
    public boolean grounded = true;

    public boolean attacking = false;
    private int state = 0;
    public float time = 0.0f;
    public boolean isLooping = true;

    public void set(int newState) {
        state = newState;
        time = 0.0f;
    }

    public int get() {
        return state;
    }

    public String getByID(int id) {
        switch (id) {
            case STATE_NORMAL:
                return "STATE_NORMAL";
            case STATE_FALLING:
                return "STATE FALLING";
            case STATE_JUMPING:
                return "STATE JUMPING";
            case STATE_RIGHT:
                return "STATE RIGHT";
            case STATE_LEFT:
                return "STATE LEFT";
            case STATE_HIT:
                return "STATE HIT";
            case STATE_DEAD:
                return "STATE DEAD";
        }
        return "unknown state";
    }
}
