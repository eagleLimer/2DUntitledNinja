package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.game.*;

import static com.mygdx.game.game.MyGdxGame.worldHeight;
import static com.mygdx.game.game.MyGdxGame.worldWidth;

public class EditState extends GameState {
    private static final int CAMERA_SPEED = 400;
    private static final float ZOOM_SPEED = 0.03f;
    private Stage menuStage;
    private Viewport menuviewport;
    private SpriteBatch batch;
    private Stage stage;
    private OrthographicCamera camera;
    private OrthographicCamera menucamera;
    private Level level;
    private OrthogonalTiledMapRenderer renderer;
    private BitmapFont font;
    private String coordinates;
    private KeyboardController controller;
    private InputMultiplexer inputMultiplexer;
    private int currentTileId = 5;
    private int mouseX;
    private int mouseY;
    private float zoom;
    private Table mainTable;
    private String currentLayerName = Map.COLLISION_LAYER_NAME;
    private Table sideTable;

    //todo: disable map drawing while in table area.
    public EditState(final StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        menucamera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, camera);
        menuviewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, menucamera);

        viewport.apply();
        menuviewport.apply();
        stage = new Stage(viewport, batch);
        menuStage = new Stage(menuviewport, batch);

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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

        zoom = 1;
    }

    public EditState(StateChangeListener stateChangeListener, String fileName) {
        this(stateChangeListener);
        level = new Level();
        level.loadLevel(fileName);
        renderer = new OrthogonalTiledMapRenderer(level.map);


    }

    public EditState(StateChangeListener stateChangeListener, String fileName, int mapWidth, int mapHeight) {
        this(stateChangeListener);
        level = new Level();
        level.newLevel(fileName, mapWidth, mapHeight);
        renderer = new OrthogonalTiledMapRenderer(level.map);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        camera.update();
        renderer.setView(camera);
        renderer.render();
        coordinates = "X: " + String.valueOf(mouseX) + "  Y: " + String.valueOf(mouseY);
        batch.begin();
        font.draw(batch, coordinates, 100, 100);
        font.draw(batch, currentLayerName, 500, 100);
        batch.end();
        menuStage.act();
        menuStage.draw();
    }

    @Override
    public void update(float step) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mouseX = Gdx.input.getX();
        mouseY = Gdx.input.getY();
        // this should be moved to Map.screenToMapCoords() however I like to have the real coords here to print while I'm testing stuff :)
        Vector2 vector2 = viewport.unproject(new Vector2(mouseX, mouseY));
        mouseX = (int) vector2.x;
        mouseY = (int) vector2.y;

        //todo: add all buttons to a list and check isOver() method and disable changeTile if true
        if (level.map.mouseInbounds(mouseX, mouseY)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                level.map.changeTile(mouseX, mouseY, currentTileId, currentLayerName);
            }
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

    private void createTileTable() {
        //todo: alternative way and also good for future.
        /*ImageButton button = new ImageButton(MyGdxGame.uiSkin);
        TiledMapTileSet set = Map.loadTileSet(Map.TILE_SET_NAME, Map.TILE_SET_WIDTH, Map.TILE_SET_HEIGHT);
        TextureRegion region = set.getTile(0).getTextureRegion();
        button.getStyle().imageUp = new TextureRegionDrawable(region);*/
        sideTable = new Table();
        sideTable.setFillParent(true);
        sideTable.bottom();
        final TiledMapTileSet tileSet = Map.loadTileSet(Map.TILE_SET_NAME, Map.TILE_SET_WIDTH, Map.TILE_SET_HEIGHT);
        int TileIndex = 0;
        for (int i = 0; i < Map.TILE_SET_HEIGHT; i++) {
            for (int j = 0; j < Map.TILE_SET_WIDTH; j++) {
                TiledMapTile currentTile = tileSet.getTile(TileIndex);
                if (currentTile.getTextureRegion() != null) {

                    Drawabletile drawabletile = new Drawabletile(currentTile.getTextureRegion());
                    ImageButton tileButton = new ImageButton(drawabletile);
                    //why this doesnt work I have no clue, but maybe it would look better idk.
                    /*ImageButton tileButton = new ImageButton(MyGdxGame.uiSkin);
                    tileButton.getStyle().imageUp = new TextureRegionDrawable(currentTile.getTextureRegion());*/
                    tileButton.addListener(new TileClickListener(currentTile.getId()) {
                        public void clicked(InputEvent event, float x, float y) {
                            currentTileId = getId();
                        }
                    });
                    ImageButton newButton = tileButton;
                    sideTable.add(newButton);
                }
                TileIndex++;
            }
            sideTable.row();
        }
        menuStage.addActor(sideTable);

    }

    private void createMainTable() {
        mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.right();


        //Create buttons
        TextButton saveLevelButton = new TextButton("save level", MyGdxGame.uiSkin);
        TextButton exitButton = new TextButton("Back", MyGdxGame.uiSkin);
        TextButton removeTileButton = new TextButton("Remove tiles", MyGdxGame.uiSkin);
        TextButton visualLayerButton = new TextButton("Visual layer", MyGdxGame.uiSkin);
        TextButton collisionLayerButton = new TextButton("Collision layer", MyGdxGame.uiSkin);
        TextButton hideLayerButton = new TextButton("Hide layer", MyGdxGame.uiSkin);


        //Add listeners to buttons

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

        //Add buttons to table
        mainTable.padTop(100);
        mainTable.add(removeTileButton);
        mainTable.row();
        mainTable.add(collisionLayerButton);
        mainTable.row();
        mainTable.add(visualLayerButton);
        mainTable.row();
        mainTable.add(hideLayerButton);
        mainTable.row();
        mainTable.add(saveLevelButton);
        mainTable.row();
        mainTable.add(exitButton);


        //Add table to stage
        menuStage.addActor(mainTable);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        menuStage.dispose();
    }


}
