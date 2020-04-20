package com.mygdx.game.gamestates;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.*;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;
import com.mygdx.game.systems.*;


//todo: this class is getting a bit too big, maybe move createEngine and addMapToEngine to engine.class
public class PlayState extends GameState {
    public static final String FIRST_LEVEL_NAME = "levelFiles/level2";
    private static final float GRAVITY = -9.8f*2;
    private static final int MOUNTAINS_HEIGHT_1 = 200;
    private static final int MOUNTAINS_HEIGHT_2 = 50;
    private static final int HILLS_HEIGHT = -200;
    private static final float MOUNTAINS_MOVEMENT_1 = 2;
    private static final float MOUNTAINS_MOVEMENT_2 = 1.6f;
    private static final float HILLS_MOVEMENT = 1.2f;

    private final Texture backgroundHills;
    private String levelName;
    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Level level;
    private OrthogonalTiledMapRenderer renderer;
    private KeyboardController controller;
    private Engine engine;
    private Entity player;
    private World world;
    private AnimationsRes animationsRes;
    private ImagesRes imagesRes;
    private BitmapFont font;
    private Texture backgroundTexture;
    private final Texture backgroundSky;
    private int srcx = 0;
    private float playerXPos;

    public PlayState(StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        //viewport = new ScalingViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);

        levelName = FIRST_LEVEL_NAME;
        level = new Level();
        level.loadLevel(levelName);
        renderer = new OrthogonalTiledMapRenderer(level.map);

        controller = new KeyboardController();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(controller);

        animationsRes = new AnimationsRes();
        imagesRes = new ImagesRes();

        backgroundTexture = imagesRes.backgroundImage.getTexture();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundSky = imagesRes.skyImage.getTexture();
        backgroundSky.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundHills = imagesRes.hillsImage.getTexture();
        backgroundHills.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new MyContactListener());

        createEngine();
        EntityCreator creator = new EntityCreator(engine, world);
        //if u create entity before u create player, playerCollisionSystem wont work with that entity??
        player = creator.createPlayer(5,5);
        engine.addEntity(player);
        addMapToEngine();
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 4; j++) {
                creator.createBasicEnemy( i+50, j + 2);
            }
        }
        BasicEnemyMovement enemyMovement = new BasicEnemyMovement(player);

        engine.addSystem(enemyMovement);

        creator.createBoss(70,45);
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

        playerXPos = player.getComponent(PositionComponent.class).position.x *Tile.tileSize;
        camera.position.set(playerXPos, player.getComponent(PositionComponent.class).position.y * Tile.tileSize+MyGdxGame.worldHeight/5, 0);

        //camVector.x = camVector.x*32;
        camera.update();

        drawBackground();

        renderer.setView(camera);
        renderer.render();
        engine.render();
        /*batch.begin();
        font.draw(batch, player.getComponent(StateComponent.class).getByID(player.getComponent(StateComponent.class).get()), 100, 100);
        batch.end();*/
    }

    private void drawBackground() {
        batch.begin();
        batch.draw(backgroundSky, 0, viewport.getScreenHeight() - imagesRes.skyImage.getRegionHeight(), srcx++/*(int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/4*/, 0, viewport.getScreenWidth(), imagesRes.skyImage.getRegionHeight());
        batch.draw(backgroundTexture, 0, MOUNTAINS_HEIGHT_1, (int) (playerXPos / MOUNTAINS_MOVEMENT_1), 0, viewport.getScreenWidth(), imagesRes.backgroundImage.getRegionHeight());
        batch.draw(backgroundTexture, 0, MOUNTAINS_HEIGHT_2, (int) (playerXPos / MOUNTAINS_MOVEMENT_2), 0, viewport.getScreenWidth(), imagesRes.backgroundImage.getRegionHeight());
        batch.draw(backgroundHills, 0, HILLS_HEIGHT, (int) (playerXPos / HILLS_MOVEMENT), 0, viewport.getScreenWidth(), imagesRes.hillsImage.getRegionHeight());
        batch.end();
    }

    public void menuRender() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        drawBackground();
        renderer.setView(camera);
        renderer.render();
        engine.render();
    }

    @Override
    public void update(float step) {
        if (controller.esc) {
            controller.esc = false;
            stateChangeListener.pushState(new PlayMenu(stateChangeListener, this));
        }
        engine.update(step);
        if(player.getComponent(HealthComponent.class).health <= 0){
            stateChangeListener.popState();
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width,height, true);
        //stage.getViewport().update(width,height,true);
        //todo: handle background zoom.
    }

    private void createEngine() {
        PlayerCollisionSystem collisionSystem = new PlayerCollisionSystem();
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(controller);
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        AnimationSystem animationSystem = new AnimationSystem();
        MyEntityListener entityListener = new MyEntityListener(world);

        engine = new Engine(viewport, batch);
        engine.addEntityListener(entityListener);
        engine.addSystem(collisionSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(animationSystem);
    }

    private void addMapToEngine() {

        BodyCreator bodyCreator = new BodyCreator(world);
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        PositionComponent position = engine.createComponent(PositionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);

        for (int col = 0; col < level.map.getMapHeight(); col++) {
            for (int row = 0; row < level.map.getMapWidth(); row++) {

                if (level.map.getCell(row * Tile.tileSize, col * Tile.tileSize, Map.COLLISION_LAYER_NAME).getTile() != null) {
                    /*TextureComponent texture = engine2.createComponent(TextureComponent.class);
                    texture.region = level.map.getCell(row*Tile.tileSize, col*Tile.tileSize, Map.COLLISION_LAYER_NAME).getTile().getTextureRegion();
                    mapTile.add(texture);*/
                    Entity mapTile = engine.createEntity();
                    type.type = TypeComponent.SCENERY;
                    position.position.set(row, col, 0);
                    bodyComponent.body = bodyCreator.makeRectBody(position.position.x + 0.5f, position.position.y + 0.5f, 1, 1, BodyMaterial.METAL,
                            BodyDef.BodyType.StaticBody, true);
                    bodyComponent.body.setUserData(mapTile);

                    mapTile.add(type);
                    mapTile.add(position);
                    mapTile.add(bodyComponent);
                    engine.addEntity(mapTile);
                }
            }
        }
    }
}
