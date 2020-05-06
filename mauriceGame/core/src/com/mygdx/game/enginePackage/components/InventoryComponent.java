package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.Item;

public class InventoryComponent implements Component {
    public Array<Item> items;
    public int coins;

    public InventoryComponent() {
        items = new Array<>();
        coins = 0;
    }
}
