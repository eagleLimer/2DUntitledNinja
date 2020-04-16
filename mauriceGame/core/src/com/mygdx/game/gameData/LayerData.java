package com.mygdx.game.gameData;

public class LayerData {
    private int mapWidth;
    private int mapHeight;
    private int[] tileIdList;


    public void setTileIdList(int[] tileIdList) {
        this.tileIdList = tileIdList;
    }


    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getWidth() {
        return mapWidth;
    }

    public int getHeight() {
        return mapHeight;
    }

    public int[] getTileIdList() {
        return tileIdList;
    }
}
