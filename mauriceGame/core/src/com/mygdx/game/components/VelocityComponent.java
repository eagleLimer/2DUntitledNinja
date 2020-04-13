package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class VelocityComponent implements Component {
    public float sprintSpeed;
    public float jumpForce;
    public boolean canJump;
    public float jumpCooldown;
    public float jumpCountDown;
}
