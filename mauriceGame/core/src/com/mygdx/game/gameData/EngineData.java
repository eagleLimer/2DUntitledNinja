package com.mygdx.game.gameData;

public class EngineData {
    private int entitiesLength;
    private EntityData[] entitiesDataList;
    private LevelSensorData[] levelSensorDataList;
    private int playerStartX;
    private int playerStartY;

    public int getEntitiesLength() {
        return entitiesLength;
    }

    public void setEntitiesLength(int entitiesLength) {
        this.entitiesLength = entitiesLength;
    }

    public EntityData[] getEntitiesDataList() {
        return entitiesDataList;
    }

    public void setEntitiesDataList(EntityData[] entitiesDataList) {
        this.entitiesDataList = entitiesDataList;
    }

    public int getPlayerStartX() {
        return playerStartX;
    }

    public void setPlayerStartX(int playerStartX) {
        this.playerStartX = playerStartX;
    }

    public int getPlayerStartY() {
        return playerStartY;
    }

    public void setPlayerStartY(int playerStartY) {
        this.playerStartY = playerStartY;
    }

    public LevelSensorData[] getLevelSensorDataList() {
        return levelSensorDataList;
    }

    public void setLevelSensorDataList(LevelSensorData[] levelSensorDataList) {
        this.levelSensorDataList = levelSensorDataList;
    }
}
