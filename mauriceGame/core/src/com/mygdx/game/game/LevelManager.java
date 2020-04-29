package com.mygdx.game.game;

public class LevelManager {
    public boolean newLevel = false;
    private static final String FILE_PATH = "levelFiles/";
    private String nextLevelName;
    public void loadLevel(String nextLevelName){
        this.nextLevelName = FILE_PATH + nextLevelName;
        newLevel = true;
    }
    public String getNextLevelName(){
        return nextLevelName;
    }

}
