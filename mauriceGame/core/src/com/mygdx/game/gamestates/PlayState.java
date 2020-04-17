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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.*;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;
import com.mygdx.game.systems.*;

import static com.mygdx.game.game.MyGdxGame.worldHeight;
import static com.mygdx.game.game.MyGdxGame.worldWidth;

public class PlayState extends GameState {

    public static final String FIRST_LEVEL_NAME = "levelFiles/level2";
    private static final float GRAVITY = -9.8f*2;
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

    public PlayState(StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, camera);
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

        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new MyContactListener());

        CreateEngine();
        EntityCreator creator = new EntityCreator(engine, world);
        creator.CreateBoss(70,45);
        player = creator.CreatePlayer(5,5);
        engine.addEntity(player);
        AddMapToEngine();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 100; j++) {
                creator.CreateBasicEnemy( 3+100, i + 2);
            }
        }
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
        camera.position.set(player.getComponent(PositionComponent.class).position.x * Tile.tileSize, player.getComponent(PositionComponent.class).position.y * Tile.tileSize+worldHeight/5, 0);
        camera.update();
        batch.begin();
        batch.draw(backgroundSky,0,viewport.getScreenHeight()-imagesRes.skyImage.getRegionHeight(), srcx++/*(int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/4*/ , 0, viewport.getScreenWidth(),imagesRes.skyImage.getRegionHeight());
        batch.draw(backgroundTexture,0,0, (int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/2 , 0, viewport.getScreenWidth(),imagesRes.backgroundImage.getRegionHeight());
        batch.end();
        renderer.setView(camera);
        renderer.render();
        engine.render();
        batch.begin();
        font.draw(batch, player.getComponent(StateComponent.class).getByID(player.getComponent(StateComponent.class).get()), 100, 100);
        batch.end();
    }

    public void menuRender() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.begin();
        batch.draw(backgroundSky,0,viewport.getScreenHeight()-imagesRes.skyImage.getRegionHeight(), srcx++/*(int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/4*/ , 0, viewport.getScreenWidth(),imagesRes.skyImage.getRegionHeight());
        batch.draw(backgroundTexture,0,0, (int)(player.getComponent(PositionComponent.class).position.x*Tile.tileSize)/2 , 0, viewport.getScreenWidth(),imagesRes.backgroundImage.getRegionHeight());
        batch.end();
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
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        //todo: handle background zoom.
    }

    private void CreateEngine() {
        CollisionSystem collisionSystem = new CollisionSystem(level.map);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(controller);
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        AnimationSystem animationSystem = new AnimationSystem();
        engine = new Engine(viewport, batch);
        engine.addSystem(collisionSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(animationSystem);
    }

    private void AddMapToEngine() {

        BodyCreator bodyCreator = new BodyCreator(world);
        BodyComponent bodyComponent;
        PositionComponent position;
        TypeComponent type;

        for (int col = 0; col < level.map.getMapHeight(); col++) {
            for (int row = 0; row < level.map.getMapWidth(); row++) {

                if (level.map.getCell(row * Tile.tileSize, col * Tile.tileSize, Map.COLLISION_LAYER_NAME).getTile() != null) {
                    bodyComponent = engine.createComponent(BodyComponent.class);
                    position = engine.createComponent(PositionComponent.class);
                    type = engine.createComponent(TypeComponent.class);
                    //TextureComponent texture = engine.createComponent(TextureComponent.class);
                    //texture.region = level.map.getCell(row * Tile.tileSize, col * Tile.tileSize).getTile().getTextureRegion();
                    //mapTile.add(texture);

                    Entity mapTile = engine.createEntity();

                    type.type = TypeComponent.SCENERY;
                    position.position.set(row, col, 0);
                    bodyComponent.body = bodyCreator.makeRectBody(position.position.x + 0.5f, position.position.y + 0.5f, 1, 1, BodyMaterial.METAL,
                            BodyDef.BodyType.KinematicBody, true);
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
