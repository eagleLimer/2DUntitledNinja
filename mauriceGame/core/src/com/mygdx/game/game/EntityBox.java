package com.mygdx.game.game;

public class EntityBox {
    private int id;
    private String name;

    public EntityBox(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
    public int getId(){
        return id;
    }
}
