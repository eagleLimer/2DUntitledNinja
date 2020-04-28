package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class DamageComponent implements Component {
    public float damage;
    public float damageTimer = 0;
    public float damageCD = 0.5f;
}
