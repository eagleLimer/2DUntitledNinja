package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Base64Coder;
import com.mygdx.game.gameData.LayerData;
import com.mygdx.game.resources.ImagesRes;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class Map extends TiledMap {
    private int mapHeight;
    private int mapWidth;

    public static final String TILE_SET_NAME = "TileSet.png";
    public static final int TILE_SET_WIDTH = 10;
    public static final int TILE_SET_HEIGHT = 5;
    public static final String COLLISION_LAYER_NAME = "collisionLayer";
    public static final String COLLISION_LAYER_PATH = "/collisionLayer.json";
    public static final String VISUAL_LAYER_NAME = "visualLayer";
    public static final String VISUAL_LAYER_PATH = "/visualLayer.json";


    public Map() {
        super();
        this.getTileSets().addTileSet(loadTileSet(TILE_SET_NAME, TILE_SET_WIDTH, TILE_SET_HEIGHT));
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public Tile getTile(int x, int y, String tileLayerName) {
        TiledMapTileLayer layer = (TiledMapTileLayer) this.getLayers().get(tileLayerName);
        return (Tile) layer.getCell(x / Tile.tileSize, y / Tile.tileSize).getTile();
    }

    public TiledMapTileLayer.Cell getCell(int x, int y, String tileLayerName) {
        TiledMapTileLayer layer = (TiledMapTileLayer) this.getLayers().get(tileLayerName);
        return layer.getCell(x / Tile.tileSize, y / Tile.tileSize);
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void newMap(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        TiledMapTileLayer visualLayer = createNewLayer(mapWidth, mapHeight);
        visualLayer.setName(VISUAL_LAYER_NAME);
        TiledMapTileLayer collisionLayer = createNewLayer(mapWidth, mapHeight);
        collisionLayer.setName(COLLISION_LAYER_NAME);

        /*Texture texture = new Texture(Gdx.files.internal("meerkat.jpg"));
        TextureRegion region = new TextureRegion(texture, 0, 0, 1920, 1080);
        TiledMapImageLayer backgroundLayer = new TiledMapImageLayer(region, 0, 0);
        backgroundLayer.setName("layer2");
        backgroundLayer.setVisible(true);

        this.getLayers().add(backgroundLayer);*/
        this.getLayers().add(visualLayer);
        this.getLayers().add(collisionLayer);
    }

    private TiledMapTileLayer createNewLayer(int mapWidth, int mapHeight) {
        TiledMapTileLayer newLayer = new TiledMapTileLayer(mapWidth, mapHeight, Tile.tileSize, Tile.tileSize);
        for (int j = 0; j < mapHeight; j++) {
            for (int i = 0; i < mapWidth; i++) {
                final TiledMapTileLayer.Cell tmpCell = new TiledMapTileLayer.Cell();
                newLayer.setCell(i, j, tmpCell);
            }
        }
        return newLayer;
    }

    public void saveToFile(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName + COLLISION_LAYER_PATH);
        saveLayer(fileHandle, COLLISION_LAYER_NAME);
        FileHandle secondHandle = Gdx.files.local(fileName + VISUAL_LAYER_PATH);
        saveLayer(secondHandle, VISUAL_LAYER_NAME);
    }

    private void saveLayer(FileHandle fileHandle, String layerName) {
        LayerData layerData = new LayerData();
        int[] tileIdList = new int[mapWidth * mapHeight];
        TiledMapTileLayer currentLayer = (TiledMapTileLayer) this.getLayers().get(layerName);
        int index = 0;
        for (int j = 0; j < mapHeight; j++) {
            for (int i = 0; i < mapWidth; i++) {
                if (currentLayer.getCell(i, j).getTile() != null) {
                    tileIdList[index] = currentLayer.getCell(i, j).getTile().getId();
                } else {
                    tileIdList[index] = -1;
                }
                index++;
            }
        }
        layerData.setMapWidth(mapWidth);
        layerData.setMapHeight(mapHeight);
        layerData.setTileIdList(tileIdList);
        fileHandle.writeString(Base64Coder.encodeString(json.toJson(layerData)), false);
    }

    public void loadMap(String fileName) {

        FileHandle fileHandle = Gdx.files.local(fileName + COLLISION_LAYER_PATH);
        TiledMapTileLayer collisionLayer = loadLayer(fileHandle);
        collisionLayer.setName(COLLISION_LAYER_NAME);

        FileHandle secondHandle = Gdx.files.local(fileName + VISUAL_LAYER_PATH);
        TiledMapTileLayer visualLayer = loadLayer(secondHandle);
        visualLayer.setName(VISUAL_LAYER_NAME);

        /*ImagesRes res = new ImagesRes();
        TextureRegion region = res.backgroundImage;
        TiledMapImageLayer backgroundLayer = new TiledMapImageLayer(region, 0, 0);
        backgroundLayer.setName("layer2");
        backgroundLayer.setVisible(true);
        this.getLayers().add(backgroundLayer);*/
        this.getLayers().add(visualLayer);
        this.getLayers().add(collisionLayer);
    }

    private TiledMapTileLayer loadLayer(FileHandle fileHandle) {

        LayerData layerData = json.fromJson(LayerData.class, Base64Coder.decodeString(fileHandle.readString()));
        this.mapWidth = layerData.getWidth();
        this.mapHeight = layerData.getHeight();
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, Tile.tileSize, Tile.tileSize);
        int index = 0;
        int[] tileIdList = layerData.getTileIdList();
        for (int col = 0; col < mapHeight; col++) {
            for (int row = 0; row < mapWidth; row++) {
                int currentTileId = tileIdList[index++];
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                if (currentTileId != -1) {
                    cell.setTile(this.getTileSets().getTile(currentTileId));
                }
                mapLayer.setCell(row, col, cell);
            }
        }
        return mapLayer;
    }

    public static TiledMapTileSet loadTileSet(String tileSetFilePath, int tileSetWidth, int tileSetHeight) {
        TiledMapTileSet tileSet = new TiledMapTileSet();
        tileSet.setName("set1");
        Tile[] tileList = new Tile[tileSetWidth * tileSetHeight];
        Texture texture = new Texture(Gdx.files.internal(tileSetFilePath));

        TextureRegion region;
        int currentTile = 0;
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                region = new TextureRegion(texture, j * Tile.tileSize, i * Tile.tileSize, Tile.tileSize, Tile.tileSize);
                tileList[currentTile] = new Tile();
                tileList[currentTile].setId(currentTile);
                tileList[currentTile].setTextureRegion(region);
                tileSet.putTile(currentTile, tileList[currentTile++]);
            }
        }
        return tileSet;
    }

    public void changeTile(int x, int y, int tileSetId, String currentLayer) {
        TiledMapTileLayer.Cell cell = getCell(x, y, currentLayer);
        if (cell != null) {
            if (tileSetId == -1) {
                cell.setTile(null);
            } else {
                cell.setTile(this.getTileSets().getTile(tileSetId));
            }
        }
    }

    public boolean mouseInbounds(int mouseX, int mouseY) {
        return mouseX > 0 && mouseY > 0 && mouseX < mapWidth * Tile.tileSize && mouseY < mapHeight * Tile.tileSize;
    }

}
