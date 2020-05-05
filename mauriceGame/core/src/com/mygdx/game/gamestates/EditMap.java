package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.game.*;
import com.mygdx.game.resources.ImagesRes;

import static com.mygdx.game.game.MyGdxGame.*;

//very big class, but tables are about 180 lines
public class EditMap extends GameState {
    private static final int CAMERA_SPEED = 400;
    private static final float ZOOM_SPEED = 0.03f;
    private final SpriteBatch menuBatch;
    private Stage menuStage;
    private Viewport menuviewport;
    private SpriteBatch batch;
    private Stage stage;
    private OrthographicCamera camera;
    private OrthographicCamera menucamera;
    private Level level;
    private BitmapFont font;
    private String coordinates;
    private KeyboardController controller;
    private InputMultiplexer inputMultiplexer;
    private int currentTileId = 5;
    private int mouseX;
    private int mouseY;
    private float zoom;
    private int mouseSize;
    private Table mainTable;
    private String currentLayerName = Map.COLLISION_LAYER_NAME;
    private Table sideTable;
    private TextureRegion currentTileRegion;
    private LevelManager levelManager;

    public EditMap(final StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();
        menuBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        menucamera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, camera);
        menuviewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, menucamera);

        levelManager = new LevelManager();
        viewport.apply();
        menuviewport.apply();
        stage = new Stage(viewport, batch);
        menuStage = new Stage(menuviewport, menuBatch);

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        zoom = 1;
        mouseSize = 1;
        menucamera.position.set(menucamera.viewportWidth / 2, menucamera.viewportHeight / 2, 0);
        menucamera.update();
        font = new BitmapFont();
        controller = new KeyboardController();

        createMainTable();
        createTileTable();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(menuStage);
        inputMultiplexer.addProcessor(controller);
    }

    public EditMap(StateChangeListener stateChangeListener, String fileName) {
        this(stateChangeListener);
        level = new Level(fileName, controller, viewport, levelManager);
        currentTileRegion = level.map.getTileSets().getTile(currentTileId).getTextureRegion();
    }

    public EditMap(StateChangeListener stateChangeListener, String fileName, int mapWidth, int mapHeight) {
        this(stateChangeListener);
        level = new Level(fileName,mapWidth,mapHeight,controller, viewport, levelManager);
        currentTileRegion = level.map.getTileSets().getTile(currentTileId).getTextureRegion();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        level.render(camera, batch);
        coordinates = "X: " + String.valueOf(mouseX/RENDERUNITS_PER_METER) + "  Y: " + String.valueOf(mouseY/RENDERUNITS_PER_METER);
        menuBatch.begin();
        font.draw(menuBatch, coordinates, 100, 100);
        font.draw(menuBatch, currentLayerName, worldWidth-100, worldHeight-100);
        menuBatch.end();
        batch.begin();
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 0.5f);
        for (int i = 0; i < mouseSize; i++) {
            for (int j = 0; j < mouseSize; j++) {
                batch.draw(currentTileRegion,(int)((mouseX-(mouseSize-1)* RENDERUNITS_PER_METER/2 + i*RENDERUNITS_PER_METER)/RENDERUNITS_PER_METER)*RENDERUNITS_PER_METER,
                        (int)((mouseY-(mouseSize-1)* RENDERUNITS_PER_METER/2 + j*RENDERUNITS_PER_METER)/RENDERUNITS_PER_METER)*RENDERUNITS_PER_METER);

            }
        }
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1f);
        menuStage.act();
        menuStage.draw();
    }

    @Override
    public void update(float step) {

        mouseX = Gdx.input.getX();
        mouseY = Gdx.input.getY();
        // this should be moved to Map.screenToMapCoords() however I like to have the real coords here to print while I'm testing stuff :)
        Vector2 mouseVector = viewport.unproject(new Vector2(mouseX, mouseY));
        mouseX = (int) mouseVector.x;
        mouseY = (int) mouseVector.y;

        //todo: add all buttons to a list and check isOver() method and disable changeTile if true... might not be needed but is definitely an option
        //if (level.map.mouseInbounds(mouseX, mouseY)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !mouseAtTable(Gdx.input.getX(),Gdx.input.getY(), mainTable) &&
                    !mouseAtTable(Gdx.input.getX(),Gdx.input.getY(), sideTable)) {

                for (int i = 0; i < mouseSize; i++) {
                    for (int j = 0; j < mouseSize; j++) {
                        level.map.changeTile((int)(mouseX-(mouseSize-1)* RENDERUNITS_PER_METER/2 + i*RENDERUNITS_PER_METER),
                                (int)(mouseY-(mouseSize-1)* RENDERUNITS_PER_METER/2 + j*RENDERUNITS_PER_METER),currentTileId,currentLayerName);
                    }
                }
                level.map.changeTile(mouseX, mouseY, currentTileId, currentLayerName);
            }

        if (controller.up) {
            camera.position.y += step * CAMERA_SPEED * zoom;
        }
        if (controller.down) {
            camera.position.y -= step * CAMERA_SPEED * zoom;
        }
        if (controller.left) {
            camera.position.x -= step * CAMERA_SPEED * zoom;
        }
        if (controller.right) {
            camera.position.x += step * CAMERA_SPEED * zoom;
        }
        if (controller.zoomIn) {
            zoom += ZOOM_SPEED;
            viewport.setWorldWidth(worldWidth * zoom);
            viewport.setWorldHeight(worldHeight * zoom);
            viewport.apply();
        }
        if (controller.zoomOut) {
            zoom -= ZOOM_SPEED;
            viewport.setWorldWidth(worldWidth * zoom);
            viewport.setWorldHeight(worldHeight * zoom);
            viewport.apply();
        }
    }

    private boolean mouseAtTable(float x, float y, Table table) {
        Vector2 menuMouseVector = menuviewport.unproject(new Vector2(x,y));
        float menuX = menuMouseVector.x;
        float menuY = menuMouseVector.y;
        return(menuX > table.getX() && menuX < table.getX()+table.getWidth() && menuY > table.getY() && menuY < table.getY()+table.getHeight());
    }

    private void createTileTable() {
        sideTable = new Table();
        float buttonSize = worldHeight/20;
        sideTable.bottom();

        TextButton increaseMouseSizeButton = new TextButton("+",MyGdxGame.uiSkin);
        final Label currentMouseSizeLabel = new Label(Integer.toString(mouseSize), MyGdxGame.uiSkin);
        TextButton decreaseMouseSizeButton = new TextButton("-",MyGdxGame.uiSkin);

        increaseMouseSizeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mouseSize++;
                currentMouseSizeLabel.setText(Integer.toString(mouseSize));
            }
        });
        decreaseMouseSizeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if (mouseSize > 1) {
                    mouseSize--;
                    currentMouseSizeLabel.setText(Integer.toString(mouseSize));
                }
            }
        });
        sideTable.add(increaseMouseSizeButton);
        sideTable.add(currentMouseSizeLabel);
        sideTable.add(decreaseMouseSizeButton);
        sideTable.row();
        final TiledMapTileSet tileSet = Map.loadTileSet(Map.TILE_SET_NAME, Map.TILE_SET_WIDTH, Map.TILE_SET_HEIGHT);
        int TileIndex = 0;
        for (int i = 0; i < Map.TILE_SET_HEIGHT; i++) {
            for (int j = 0; j < Map.TILE_SET_WIDTH; j++) {
                TiledMapTile currentTile = tileSet.getTile(TileIndex);
                if (currentTile.getTextureRegion() != null) {

                    Drawabletile drawabletile = new Drawabletile(currentTile.getTextureRegion());
                    ImageButton tileButton = new ImageButton(drawabletile);
                    //todo: alternative way and also good for future.
                    //why this doesnt work I have no clue, but maybe it would look better idk.
                    /*ImageButton tileButton = new ImageButton(MyGdxGame.uiSkin);
                    tileButton.getStyle().imageUp = new TextureRegionDrawable(currentTile.getTextureRegion());*/
                    tileButton.addListener(new TileClickListener(currentTile.getId()) {
                        public void clicked(InputEvent event, float x, float y) {
                            currentTileId = getId();
                            currentTileRegion = level.map.getTileSets().getTile(currentTileId).getTextureRegion();
                        }
                    });
                    ImageButton newButton = tileButton;
                    newButton.setWidth(buttonSize);
                    newButton.setHeight(buttonSize);
                    sideTable.add(newButton);

                }
                TileIndex++;
            }
            sideTable.row();
        }
        sideTable.setWidth(buttonSize*Map.TILE_SET_WIDTH);
        sideTable.setHeight(buttonSize*Map.TILE_SET_HEIGHT+ increaseMouseSizeButton.getHeight());
        sideTable.setY(0);
        sideTable.setX(worldWidth/2-(sideTable.getWidth()/2));

        ImagesRes res = new ImagesRes();
        NinePatch patch = new NinePatch(res.backgroundImage.getTexture(), 3,3,3,3);
        NinePatchDrawable background = new NinePatchDrawable(patch);
        sideTable.setBackground(background);

        menuStage.addActor(sideTable);

    }

    private void createMainTable() {
        mainTable = new Table();

        float tableWidth = worldWidth/3.8f;
        mainTable.setX(worldWidth-tableWidth);
        mainTable.setWidth(tableWidth);
        mainTable.setY(worldHeight/10);
        mainTable.right();

        TextButton saveLevelButton = new TextButton("save level", MyGdxGame.uiSkin);
        TextButton exitButton = new TextButton("Back", MyGdxGame.uiSkin);
        TextButton removeTileButton = new TextButton("Remove tiles", MyGdxGame.uiSkin);
        TextButton visualLayerButton = new TextButton("Visual layer", MyGdxGame.uiSkin);
        TextButton collisionLayerButton = new TextButton("Collision layer", MyGdxGame.uiSkin);
        TextButton hideLayerButton = new TextButton("Hide layer", MyGdxGame.uiSkin);
        TextButton editEntitiesButton = new TextButton("Edit Entities", MyGdxGame.uiSkin);


        saveLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                level.saveLevel();
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //maybe just call Gdx.app.exit(); instead :shrug:
                stateChangeListener.popState();
            }
        });
        removeTileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentTileId = -1;
                currentTileRegion = ImagesRes.eraserImage;
            }
        });
        visualLayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLayerName = Map.VISUAL_LAYER_NAME;
            }
        });
        collisionLayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLayerName = Map.COLLISION_LAYER_NAME;
            }
        });
        hideLayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (level.map.getLayers().get(currentLayerName).isVisible()) {
                    level.map.getLayers().get(currentLayerName).setVisible(false);
                } else {
                    level.map.getLayers().get(currentLayerName).setVisible(true);
                }
            }
        });
        editEntitiesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.pushState(new EditEntities(stateChangeListener, level, zoom));
            }
        });

        mainTable.add(removeTileButton);
        mainTable.row();
        mainTable.add(collisionLayerButton);
        mainTable.row();
        mainTable.add(visualLayerButton);
        mainTable.row();
        mainTable.add(hideLayerButton);
        mainTable.row();
        mainTable.add(editEntitiesButton);
        mainTable.row();
        mainTable.add(saveLevelButton);
        mainTable.row();
        mainTable.add(exitButton);

        float buttonSize = worldHeight/12;
        mainTable.setHeight(buttonSize*mainTable.getCells().size);
        for (Cell cell:mainTable.getCells()) {
            cell.width(mainTable.getWidth());
            cell.height(buttonSize);
        }
        menuStage.addActor(mainTable);
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        menuStage.dispose();
        level.dispose();
    }
}
