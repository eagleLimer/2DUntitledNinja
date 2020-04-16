package com.mygdx.game.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Level {
    public Map map;
    private String levelName;
    private static final String MAP_FILE_NAME = "/Map/";


    public Level() {
        map = new Map();
    }

    public void loadLevel(String fileName) {
        levelName = fileName;
        map.loadMap(levelName + MAP_FILE_NAME);
    }

    public void newLevel(String fileName, int mapWidth, int mapHeight) {
        levelName = fileName;
        map.newMap(mapWidth, mapHeight);
    }

    public void saveLevel() {
        map.saveToFile(levelName + MAP_FILE_NAME);
    }
}
