package com.mygdx.game.game;

public enum BodyMaterial {
    // bird??? what the frick vad för typer är ens rimligt?
    METAL(1f, 0f, 0.6f), GLASS(1f, 0, 0.8f),
    WOOD(0.6f, 0.3f, 0.3f), BIRD(10, 10, 10), BOUNCY(0.7f, 0.9f, 0);
    private float restitution;
    private float density;
    private float friction;

    private BodyMaterial(float density, float restitution, float friction) {
        this.density = density;
        this.restitution = restitution;
        this.friction = friction;
    }


    public float getDensity() {
        return density;
    }

    public float getRestitution() {
        return restitution;
    }

    public float getFriction() {
        return friction;
    }
}
