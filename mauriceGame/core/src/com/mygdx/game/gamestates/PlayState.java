package com.mygdx.game.gamestates;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.*;
import com.mygdx.game.systems.CollisionSystem;
import com.mygdx.game.systems.Engine;
import com.mygdx.game.systems.PhysicsSystem;
import com.mygdx.game.systems.PlayerControlSystem;

public class PlayState extends GameState {

    public static final String FIRST_LEVEL_NAME = "levelFiles/level3";
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

    TextureRegion backGroundImage;


    public PlayState(StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
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
        Texture texture = new Texture(Gdx.files.internal("meerkat.jpg"));
        backGroundImage = new TextureRegion(texture,0,0,1920,1080);

        world = new World(new Vector2(0, -9.8f*1.8f),true);
        world.setContactListener(new MyContactListener());

        CreateEngine();
        CreatePlayer();
        AddMapToEngine();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(player.getComponent(PositionComponent.class).position.x*Tile.tileSize, player.getComponent(PositionComponent.class).position.y*Tile.tileSize, 0);
        camera.update();
        batch.begin();
        //batch.draw(backGroundImage,0,0);
        batch.end();

        renderer.setView(camera);
        renderer.render();
        engine.render();
    }

    public void menuRender() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        renderer.setView(camera);
        renderer.render();
        engine.render();

    }

    @Override
    public void update(float step) {
        if (controller.esc) {
            controller.esc = false;
            stateChangeListener.pushState(new PlayMenu(stateChangeListener,this));
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

    private void CreateEngine() {
        CollisionSystem collisionSystem = new CollisionSystem(level.map);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(controller);
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        engine = new Engine(viewport, batch);
        engine.addSystem(collisionSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(physicsSystem);
    }

    private void CreatePlayer() {
        player = engine.createEntity();
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        PositionComponent position = engine.createComponent(PositionComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        Texture texture1 = new Texture(Gdx.files.internal("charRight.png"));
        BodyCreator bodyCreator = new BodyCreator(world);
        position.position.set(100,100,0);
        bodyComponent.body = bodyCreator.makeRectBody(position.position.x, position.position.y, 32,  32, BodyMaterial.WOOD,
                BodyDef.BodyType.DynamicBody,false);
        texture.region = new TextureRegion(texture1,0,0,32,32);
        player.add(position);
        player.add(texture);
        player.add(bodyComponent);
        player.add(engine.createComponent(PlayerComponent.class));
        player.add(engine.createComponent(StateComponent.class));

        engine.addEntity(player);
    }

    private void AddMapToEngine() {

        BodyCreator bodyCreator = new BodyCreator(world);
        BodyComponent bodyComponent;
        PositionComponent position;

        for (int col = 0; col < level.map.getMapHeight(); col++) {
            for (int row = 0; row < level.map.getMapWidth(); row++) {

                if(level.map.getCell(row*Tile.tileSize,col*Tile.tileSize).getTile() != null) {
                    bodyComponent = engine.createComponent(BodyComponent.class);
                    position = engine.createComponent(PositionComponent.class);
                    //TextureComponent texture = engine.createComponent(TextureComponent.class);
                    //texture.region = level.map.getCell(row * Tile.tileSize, col * Tile.tileSize).getTile().getTextureRegion();
                    //mapTile.add(texture);

                    Entity mapTile = engine.createEntity();

                    position.position.set(row * Tile.tileSize, col * Tile.tileSize, 0);
                    bodyComponent.body = bodyCreator.makeRectBody(position.position.x, position.position.y, 32, 32, BodyMaterial.METAL,
                            BodyDef.BodyType.StaticBody, true);

                    mapTile.add(position);
                    mapTile.add(bodyComponent);
                    engine.addEntity(mapTile);
                }
            }
        }
    }


}
