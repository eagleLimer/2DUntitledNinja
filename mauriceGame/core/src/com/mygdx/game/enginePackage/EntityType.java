package com.mygdx.game.enginePackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.resources.ImagesRes;

public enum EntityType {
    PLAYER(0, "PLAYER", ImagesRes.playerImage), BASIC_ENEMY(1, "BASIC ENEMY", ImagesRes.entityImage), BOSS(2, "BOSS", ImagesRes.bossImage),
    ROCK(3, "ROCK", ImagesRes.rockImage), PLANT_ENEMY(4, "PLANT", ImagesRes.plantImage), LEVEL_SENSOR(6, "LEVEL SENSOR", ImagesRes.levelImage),
    GHOST(5,"GHOST", ImagesRes.ghostImage);
    private int ID;
    private String name;
    private TextureRegion region;
    private EntityType(int ID, String name, TextureRegion region){
        this.ID = ID;
        this.name = name;
        this.region = region;
    }
    public static EntityType getByID(int ID){
        switch (ID){
            case 0:
                return PLAYER;
            case 1:
                return BASIC_ENEMY;
            case 2:
                return BOSS;
            case 3:
                return ROCK;
            case 4:
                return PLANT_ENEMY;
            case 5:
                return GHOST;
            case 6:
                return LEVEL_SENSOR;
            default:
                return ROCK;
        }
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public TextureRegion getRegion() {
        return region;
    }
}
