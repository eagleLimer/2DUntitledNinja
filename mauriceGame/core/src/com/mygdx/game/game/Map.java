package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Base64Coder;
import com.mygdx.game.gameData.MapData;

import java.util.HashMap;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class Map extends TiledMap {
    private int mapHeight;
    private int mapWidth;
    private String tileLayerName = "layer1";

    public static final String TILE_SET_NAME = "TileSet.png";
    public static final int TILE_SET_WIDTH = 10;
    public static final int TILE_SET_HEIGHT = 5;


    public Map(){
        super();
        this.getTileSets().addTileSet(loadTileSet(TILE_SET_NAME,TILE_SET_WIDTH,TILE_SET_HEIGHT));
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

    public Tile getTile(int x, int y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) this.getLayers().get(tileLayerName);
        return (Tile)layer.getCell(x / Tile.tileSize, y / Tile.tileSize).getTile();
    }

    public TiledMapTileLayer.Cell getCell(int x, int y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) this.getLayers().get(tileLayerName);
        return layer.getCell(x / Tile.tileSize, y / Tile.tileSize);
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void newMap(String mapName, int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, Tile.tileSize, Tile.tileSize);
        for (int col = 0; col < mapHeight; col++) {
            for (int row = 0; row < mapWidth; row++) {
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                //cell.setTile(this.getTileSets().getTile(0));
                mapLayer.setCell(row, col, cell);
            }
        }
        mapLayer.setName("layer1");

        Texture texture = new Texture(Gdx.files.internal("meerkat.jpg"));
        TextureRegion region = new TextureRegion(texture, 0,0,1920,1080);
        TiledMapImageLayer backgroundLayer = new TiledMapImageLayer(region,0,0);
        backgroundLayer.setName("layer2");
        backgroundLayer.setVisible(true);


        this.getLayers().add(backgroundLayer);
        this.getLayers().add(mapLayer);
    }

    public void saveToFile(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName);

        MapData mapData = new MapData();
        int[] tileIdList = new int[mapWidth * mapHeight];
        System.out.println("width: " + mapWidth);
        System.out.println("height: " + mapHeight);
        System.out.println(tileIdList.length);
        TiledMapTileLayer mapLayer = (TiledMapTileLayer) this.getLayers().get("layer1");
        for (int j = 0; j < mapHeight; j++) {
            for (int i = 0; i < mapWidth; i++) {
                if(mapLayer.getCell(i,j).getTile() != null) {
                    tileIdList[j * mapWidth + i] = mapLayer.getCell(i, j).getTile().getId();
                }else{
                    tileIdList[j * mapWidth + i] = -1;
                }
            }
        }
        mapData.setMapWidth(mapWidth);
        mapData.setMapHeight(mapHeight);
        mapData.setTileIdList(tileIdList);
        fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(mapData)), false);
    }

    public void loadMap(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName);

        MapData mapData = json.fromJson(MapData.class, Base64Coder.decodeString(fileHandle.readString()));
        this.mapWidth = mapData.getWidth();
        this.mapHeight = mapData.getHeight();
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, Tile.tileSize, Tile.tileSize);
        for (int col = 0; col < mapHeight; col++) {
            for (int row = 0; row < mapWidth; row++) {
                int currentTileId =  mapData.getIdList()[col * mapWidth + row];
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                if(currentTileId != -1) {
                    cell.setTile(this.getTileSets().getTile(currentTileId));
                }
                mapLayer.setCell(row, col, cell);
            }
        }
        mapLayer.setName("layer1");
        /*
        Texture texture = new Texture(Gdx.files.internal("meerkat.jpg"));
        TextureRegion region = new TextureRegion(texture, 0,0,1920,1080);
        TiledMapImageLayer backgroundLayer = new TiledMapImageLayer(region,0,0);
        backgroundLayer.setName("layer2");
        backgroundLayer.setVisible(true);

        this.getLayers().add(backgroundLayer);
        */
        this.getLayers().add(mapLayer);
    }

    public static TiledMapTileSet loadTileSet(String tileSetFilePath, int tileSetWidth, int tileSetHeight) {
        TiledMapTileSet tileSet = new TiledMapTileSet();
        Tile[] tileList = new Tile[tileSetWidth*tileSetHeight];
        tileSet.setName("set1");
        HashMap<Integer, Boolean> collideableMap = new HashMap<Integer, Boolean>();
        for (int i = 0; i < tileList.length; i++) {
            collideableMap.put(i,true);
        }
        collideableMap.put(0, false);
        Texture texture = new Texture(Gdx.files.internal(tileSetFilePath));

        TextureRegion region;
        int currentTile = 0;
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                region = new TextureRegion(texture, j * Tile.tileSize, i * Tile.tileSize, Tile.tileSize, Tile.tileSize);
                tileList[currentTile] = new Tile();
                tileList[currentTile].setId(currentTile);
                tileList[currentTile].setTextureRegion(region);
                tileList[currentTile].setCollideable(collideableMap.get(currentTile));
                tileSet.putTile(currentTile, tileList[currentTile++]);
            }
        }
        return tileSet;
    }

    public void changeTile(int x, int y, int tileSetId) {
        if(tileSetId == -1){
            this.getCell(x,y).setTile(null);
        }
        this.getCell(x, y).setTile(this.getTileSets().getTile(tileSetId));
    }

    public boolean mouseInbounds(int mouseX, int mouseY) {
        return mouseX > 0 &&mouseY > 0 && mouseX < mapWidth*Tile.tileSize&& mouseY < mapHeight*Tile.tileSize;
    }
}
