package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HealthComponent implements Component {
    public float maxHealth;
    public float health;
    public float healthReg;
    public HealthComponent(float maxHealth, float health, float healthReg){
        this.maxHealth = maxHealth;
        this.health = health;
        this.healthReg = healthReg;
    }
}
