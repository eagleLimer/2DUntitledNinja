package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class EnergyComponent implements Component {
    public float maxMana;
    public float mana;
    public float manaReg;
    public EnergyComponent(float maxMana, float manaReg){
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.manaReg = manaReg;
    }

}
