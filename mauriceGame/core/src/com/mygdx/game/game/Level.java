package com.mygdx.game.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Level {
    public Map map;
    private String levelName;


    public Level(){

        map = new Map();
        //borde ändra tileset String, width och height men ok för tillfället.
        map.loadTileSet("TileSet.png",10,5);
    }

    public void loadLevel(String fileName) {
        levelName = fileName;
        map.loadMap(levelName+"/map.json");
    }

    public void newLevel(String fileName, int mapWidth, int mapHeight) {
        levelName = fileName;
        map.newMap(levelName+"/map.json",mapWidth,mapHeight);
    }

    public void saveLevel() {
        map.saveToFile(levelName+"/map.json");
    }
}
