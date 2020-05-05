package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class VelocityComponent implements Component {
    public float sprintSpeed;
    public float jumpSpeed;
    public boolean canJump = true;
    public float jumpCooldown = 2;
    public float jumpCountDown = 0;

    public VelocityComponent(float sprintSpeed, float jumpSpeed, float jumpCooldown) {
        this.sprintSpeed = sprintSpeed;
        this.jumpSpeed = jumpSpeed;
        this.jumpCooldown = jumpCooldown;
    }
}
