package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.game.*;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;


//todo: this class is getting a bit too big, maybe move createEngine and addMapToEngine to engine.class
public class PlayState extends GameState {
    public static final String FIRST_LEVEL_NAME = "levelFiles/level2";
    private static final int MOUNTAINS_HEIGHT_1 = 200;
    private static final int MOUNTAINS_HEIGHT_2 = 50;
    private static final int HILLS_HEIGHT = -200;
    private static final float MOUNTAINS_MOVEMENT_1 = 2;
    private static final float MOUNTAINS_MOVEMENT_2 = 1.6f;
    private static final float HILLS_MOVEMENT = 1.2f;

    private final Texture backgroundHills;
    private final SpriteBatch batch;
    private final Stage stage;
    private Viewport backgroundViewport;
    private String levelName;
    private InputMultiplexer inputMultiplexer;
    private Stage backgroundStage;
    private OrthographicCamera camera;
    private OrthographicCamera backgroundCamera;
    private SpriteBatch backgroundBatch;
    private Level level;
    private OrthogonalTiledMapRenderer renderer;
    private KeyboardController controller;
    private AnimationsRes animationsRes;
    private BitmapFont font;
    private Texture backgroundTexture;
    private final Texture backgroundSky;
    private int srcx = 0;
    private float playerXPos;
    private float playerYPos;

    public PlayState(StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        backgroundBatch = new SpriteBatch();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        backgroundCamera = new OrthographicCamera();

        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        viewport.apply();

        backgroundViewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, backgroundCamera);
        backgroundViewport.apply();

        backgroundCamera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        backgroundCamera.update();
        camera.update();

        backgroundStage = new Stage(backgroundViewport, backgroundBatch);
        stage = new Stage(viewport, batch);

        levelName = FIRST_LEVEL_NAME;
        controller = new KeyboardController();
        level = new Level(levelName, controller);
        renderer = new OrthogonalTiledMapRenderer(level.map);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(backgroundStage);
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(controller);

        animationsRes = new AnimationsRes();

        backgroundTexture = ImagesRes.backgroundImage.getTexture();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundSky = ImagesRes.skyImage.getTexture();
        backgroundSky.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundHills = ImagesRes.hillsImage.getTexture();
        backgroundHills.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        font = new BitmapFont();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playerXPos = level.getPlayerXpos();
        playerYPos = level.getPlayerYpos();

        backgroundCamera.update();
        drawBackground();

        backgroundBatch.setProjectionMatrix(backgroundCamera.combined);
        camera.position.set(playerXPos, playerYPos/*+MyGdxGame.worldHeight/5*/, 0);
        level.render(camera, batch);
    }

    private void drawBackground() {
        backgroundBatch.begin();
        backgroundBatch.draw(backgroundSky, 0, backgroundViewport.getWorldHeight() - ImagesRes.skyImage.getRegionHeight(), srcx++/*(int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/4*/, 0, (int) backgroundViewport.getWorldWidth(), ImagesRes.skyImage.getRegionHeight());
        backgroundBatch.draw(backgroundTexture, 0, MOUNTAINS_HEIGHT_1, (int) (playerXPos / MOUNTAINS_MOVEMENT_1), 0, (int) backgroundViewport.getWorldWidth(), ImagesRes.backgroundImage.getRegionHeight());
        backgroundBatch.draw(backgroundTexture, 0, MOUNTAINS_HEIGHT_2, (int) (playerXPos / MOUNTAINS_MOVEMENT_2), 0, (int) backgroundViewport.getWorldWidth(), ImagesRes.backgroundImage.getRegionHeight());
        backgroundBatch.draw(backgroundHills, 0, HILLS_HEIGHT, (int) (playerXPos / HILLS_MOVEMENT), 0, (int) backgroundViewport.getWorldWidth(), ImagesRes.hillsImage.getRegionHeight());
        backgroundBatch.end();
    }

    @Override
    public void update(float step) {
        level.update(step, stateChangeListener);
        if (controller.esc) {
            controller.esc = false;
            stateChangeListener.pushState(new PlayMenu(stateChangeListener, this));
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        level.dispose();
        backgroundBatch.dispose();
        backgroundStage.dispose();
        stage.dispose();
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        backgroundViewport.update(width, height, true);
        //stage.getViewport().update(width,height,true);
        //todo: handle background zoom.
    }
}
