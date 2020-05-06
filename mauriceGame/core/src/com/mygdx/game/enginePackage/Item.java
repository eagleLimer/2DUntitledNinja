package com.mygdx.game.enginePackage;

public class Item {
    private ItemType itemType;

    public Item(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
//todo: write this class
    // maybe give every itemType its own class? because every item need very specific values
    // give player inventory
}
