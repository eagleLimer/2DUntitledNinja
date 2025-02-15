package com.mygdx.game.enginePackage;

public enum BodyMaterial {
    // bird??? what the frick vad för typer är ens rimligt?
    METAL(10f, 0f, 0.6f), GLASS(1f, 0, 0.4f),
    WOOD(0.6f, 0.3f, 0.3f), BULLET(0.01f, 0.1f, 10),
    BOUNCY(0.7f, 0.1f, 0.3f), ITEM(0.01f,0.1f,5);
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
