package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class DamageComponent implements Component {
    public float damage;
    public float damageTimer = 0;
    public float damageCD = 0.5f;

    public DamageComponent(float damage, float damageCD) {
        this.damage = damage;
        this.damageCD = damageCD;
    }
    public DamageComponent(float damage){
        this.damage = damage;
    }
}
