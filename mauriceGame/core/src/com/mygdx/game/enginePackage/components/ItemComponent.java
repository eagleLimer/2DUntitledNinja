package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.enginePackage.Item;

public class ItemComponent implements Component {
    public Item item;
    public ItemComponent(Item item) {
        this.item = item;
    }
}
