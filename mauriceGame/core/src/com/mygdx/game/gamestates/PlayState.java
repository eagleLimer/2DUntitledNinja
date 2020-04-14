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


public class PlayState extends GameState {

    public static final String FIRST_LEVEL_NAME = "levelFiles/level2";
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

    private static final float GRAVITY = -9.8f*1.8f;

    TextureRegion backgroundImage;


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

        animationsRes = new AnimationsRes();
        imagesRes = new ImagesRes();

        backgroundImage = imagesRes.backgroundImage;

        world = new World(new Vector2(0, GRAVITY),true);
        world.setContactListener(new MyContactListener());

        CreateEngine();
        CreatePlayer();
        AddMapToEngine();
        for (int i = 0; i < 5; i++) {

            CreateEntity(3,3);
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
        camera.position.set(player.getComponent(PositionComponent.class).position.x*Tile.tileSize, player.getComponent(PositionComponent.class).position.y*Tile.tileSize, 0);
        camera.update();
        batch.begin();
        batch.draw(backgroundImage,0,0);
        batch.end();

        renderer.setView(camera);
        renderer.render();
        engine.render();
        batch.begin();
        font.draw(batch, player.getComponent(StateComponent.class).getByID(player.getComponent(StateComponent.class).get()),100,100);
        batch.end();
    }

    public void menuRender() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.begin();
        batch.draw(backgroundImage,0,0);
        batch.end();
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

    private void CreatePlayer() {
        player = engine.createEntity();
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        PositionComponent position = engine.createComponent(PositionComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);

        Animation animation = animationsRes.playerRight;
        animationComponent.animationMap.put(StateComponent.STATE_RIGHT, animation);
        animation = animationsRes.playerLeft;
        animationComponent.animationMap.put(StateComponent.STATE_LEFT,animation);
        animation = animationsRes.playerFalling;
        animationComponent.animationMap.put(StateComponent.STATE_FALLING,animation);
        animation = animationsRes.playerNormal;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL,animation);
        animation = animationsRes.playerJumping;
        animationComponent.animationMap.put(StateComponent.STATE_JUMPING,animation);

        velocity.sprintSpeed = 6;
        velocity.jumpSpeed = 12;
        velocity.jumpCooldown = 0.5f;
        texture.region = imagesRes.playerImage;
        BodyCreator bodyCreator = new BodyCreator(world);
        position.position.set(3,3,0);
        bodyComponent.body = bodyCreator.makeCirclePolyBody(position.position.x, position.position.y, 1f, BodyMaterial.GLASS,
                BodyDef.BodyType.DynamicBody,false);
        bodyComponent.body.setUserData(player);
        type.type = TypeComponent.PLAYER;

        player.add(animationComponent);
        player.add(type);
        player.add(velocity);
        player.add(position);
        player.add(texture);
        player.add(bodyComponent);
        player.add(engine.createComponent(PlayerComponent.class));
        player.add(engine.createComponent(StateComponent.class));
        player.add(engine.createComponent(CollisionComponent.class));

        engine.addEntity(player);
    }

    private void AddMapToEngine() {

        BodyCreator bodyCreator = new BodyCreator(world);
        BodyComponent bodyComponent;
        PositionComponent position;
        TypeComponent type;

        for (int col = 0; col < level.map.getMapHeight(); col++) {
            for (int row = 0; row < level.map.getMapWidth(); row++) {

                if(level.map.getCell(row*Tile.tileSize,col*Tile.tileSize).getTile() != null) {
                    bodyComponent = engine.createComponent(BodyComponent.class);
                    position = engine.createComponent(PositionComponent.class);
                    type = engine.createComponent(TypeComponent.class);
                    //TextureComponent texture = engine.createComponent(TextureComponent.class);
                    //texture.region = level.map.getCell(row * Tile.tileSize, col * Tile.tileSize).getTile().getTextureRegion();
                    //mapTile.add(texture);

                    Entity mapTile = engine.createEntity();

                    type.type = TypeComponent.SCENERY;
                    position.position.set(row , col, 0);
                    bodyComponent.body = bodyCreator.makeRectBody(position.position.x, position.position.y, 1, 1, BodyMaterial.METAL,
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
    public void CreateEntity(int posx , int posy){
        Entity mosquito = engine.createEntity();
        BodyComponent entityBody = engine.createComponent(BodyComponent.class);
        PositionComponent entityPosition = engine.createComponent(PositionComponent.class);
        TextureComponent entityTexture = engine.createComponent(TextureComponent.class);
        VelocityComponent entityVelocity = engine.createComponent(VelocityComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);

        entityVelocity.sprintSpeed = 7;
        entityVelocity.jumpSpeed = 9;

        entityTexture.region = imagesRes.entityImage;
        BodyCreator entityBodyCreator = new BodyCreator(world);
        entityPosition.position.set(posx,posy,0);
        entityBody.body = entityBodyCreator.makeCirclePolyBody(entityPosition.position.x, entityPosition.position.y, 0.5f, BodyMaterial.BOUNCY,
                BodyDef.BodyType.DynamicBody,false);
        entityBody.body.setUserData(mosquito);

        type.type = TypeComponent.ENEMY;

        mosquito.add(type);
        mosquito.add(entityVelocity);
        mosquito.add(entityPosition);
        mosquito.add(entityTexture);
        mosquito.add(entityBody);
        mosquito.add(engine.createComponent(StateComponent.class));
        mosquito.add(engine.createComponent(CollisionComponent.class));
        engine.addEntity(mosquito);
    }


}
