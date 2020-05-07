package com.mygdx.game.enginePackage.components.enemyComponents;

import com.badlogic.ashley.core.Component;
public class EnemyRarityComponent implements Component {
    public int rarityLevel = 0;
    public EnemyRarityComponent(int rarityLevel) {
        this.rarityLevel = rarityLevel;
    }
}
