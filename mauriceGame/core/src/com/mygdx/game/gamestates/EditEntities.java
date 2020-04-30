package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.enginePackage.components.TypeComponent;
import com.mygdx.game.game.*;
import com.mygdx.game.resources.ImagesRes;

import java.util.HashMap;

import static com.mygdx.game.game.MyGdxGame.worldHeight;
import static com.mygdx.game.game.MyGdxGame.worldWidth;

public class EditEntities extends GameState {
    private static final int CAMERA_SPEED = 400;
    private static final float ZOOM_SPEED = 0.03f;
    private static final int ENTITY_COOLDOWN = 10;
    private final OrthographicCamera menucamera;
    private final Viewport menuviewport;
    private final Stage stage;
    private final SpriteBatch batch;
    private final Stage menuStage;
    private final KeyboardController controller;
    private final InputMultiplexer inputMultiplexer;
    private Level level;
    private OrthographicCamera camera;
    private float zoom;
    private Table mainTable;
    private int currentEntityId = 0;
    private float mouseX;
    private float mouseY;
    private int entityTimer;
    private HashMap<Integer, Texture> imageMap;
    private TextField nextLevelField;

    public EditEntities(StateChangeListener stateListener, Level level, float zoom) {
        super(stateListener);
        this.level = level;
        this.zoom = zoom;
        batch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, this.camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        menucamera = new OrthographicCamera();
        menuviewport = new FitViewport(worldWidth, MyGdxGame.worldHeight, menucamera);

        menuviewport.apply();
        stage = new Stage(this.viewport, batch);
        menuStage = new Stage(menuviewport, batch);

        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        //camera.update();
        menucamera.position.set(menucamera.viewportWidth / 2, menucamera.viewportHeight / 2, 0);
        menucamera.update();
        controller = new KeyboardController();

        createMainTable();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(menuStage);
        inputMultiplexer.addProcessor(controller);

        viewport.setWorldWidth(worldWidth * zoom);
        viewport.setWorldHeight(worldHeight * zoom);
        viewport.apply();

        imageMap = new HashMap<Integer, Texture>();
        imageMap.put(TypeComponent.PLAYER, ImagesRes.playerImage.getTexture());
        imageMap.put(TypeComponent.BASIC_ENEMY, ImagesRes.entityImage.getTexture());
        imageMap.put(TypeComponent.BALL, ImagesRes.rockImage.getTexture());
        imageMap.put(TypeComponent.BOSS, ImagesRes.bossImage.getTexture());
        imageMap.put(TypeComponent.LEVEL_SENSOR, ImagesRes.levelImage.getTexture());
    }

    private void createMainTable() {
        mainTable = new Table();
        float tableWidth = worldWidth / 3.8f;
        mainTable.setX(worldWidth - tableWidth);
        mainTable.setWidth(tableWidth);
        mainTable.setY(worldHeight / 10);
        mainTable.right();

        TextButton saveLevelButton = new TextButton("save level", MyGdxGame.uiSkin);
        TextButton exitButton = new TextButton("Back", MyGdxGame.uiSkin);
        TextButton editMapButton = new TextButton("Edit Map", MyGdxGame.uiSkin);
        TextButton removeEntitiesButton = new TextButton("Remove Entities", MyGdxGame.uiSkin);
        nextLevelField = new TextField("portal level name here", MyGdxGame.uiSkin);
        final SelectBox<EntityBox> entityBox = new SelectBox<EntityBox>(MyGdxGame.uiSkin);
        EntityBox[] entityBoxes = {new EntityBox(TypeComponent.PLAYER, "Player"),new EntityBox(TypeComponent.BASIC_ENEMY, "Basic enemy"), new EntityBox(TypeComponent.BOSS, "Boss"),
                new EntityBox(TypeComponent.BALL, "Ball"), new EntityBox(TypeComponent.LEVEL_SENSOR, "Level sensor")};

        entityBox.setItems(entityBoxes);

        entityBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentEntityId = entityBox.getSelected().getId();
                System.out.println("selected box: " + entityBox.getSelected().getId());
            }
        });
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
                stateChangeListener.popState();
            }
        });
        editMapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //maybe just call Gdx.app.exit(); instead :shrug:
                stateChangeListener.popState();
            }
        });
        removeEntitiesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentEntityId = -1;
            }
        });

        mainTable.add(entityBox);
        mainTable.row();
        mainTable.add(nextLevelField);
        mainTable.row();
        mainTable.add(removeEntitiesButton);
        mainTable.row();
        mainTable.add(editMapButton);
        mainTable.row();
        mainTable.add(saveLevelButton);
        mainTable.row();
        mainTable.add(exitButton);

        float buttonSize = worldHeight / 12;
        mainTable.setHeight(buttonSize * mainTable.getCells().size);
        for (Cell cell : mainTable.getCells()) {
            cell.width(mainTable.getWidth());
            cell.height(buttonSize);
        }
        menuStage.addActor(mainTable);
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
        Texture currentEntity = imageMap.get(currentEntityId);
        if (currentEntity != null) {
            batch.begin();
            batch.draw(currentEntity, mouseX - currentEntity.getWidth() / 2, mouseY - currentEntity.getHeight() / 2);
            batch.end();
        }
        menuStage.act();
        menuStage.draw();
    }

    @Override
    public void update(float step) {
        mouseX = Gdx.input.getX();
        mouseY = Gdx.input.getY();
        Vector2 mouseVector = viewport.unproject(new Vector2(mouseX, mouseY));
        mouseX = mouseVector.x;
        mouseY = mouseVector.y;
        entityTimer--;
        level.editUpdate(step);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !mouseAtTable(Gdx.input.getX(), Gdx.input.getY(), mainTable)) {
            if (currentEntityId == -1) {
                level.removeEntity(mouseX / Tile.tileSize, mouseY / Tile.tileSize);
            } else if (entityTimer <= 0) {
                switch (currentEntityId) {
                    case TypeComponent.LEVEL_SENSOR:
                        if (nextLevelField.getText() != "") {
                            level.setCurrentPortalName(nextLevelField.getText());
                            level.createEntity(mouseX / Tile.tileSize, mouseY / Tile.tileSize, currentEntityId);
                        }
                        break;
                    case TypeComponent.PLAYER:
                        level.setPlayerStartPos((int)mouseX/Tile.tileSize, (int)mouseY/Tile.tileSize);
                        break;
                    default:
                        level.createEntity(mouseX / Tile.tileSize, mouseY / Tile.tileSize, currentEntityId);
                        break;
                }
                entityTimer = ENTITY_COOLDOWN;
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

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        menuStage.dispose();
    }

    private boolean mouseAtTable(float x, float y, Table table) {
        Vector2 menuMouseVector = menuviewport.unproject(new Vector2(x, y));
        float menuX = menuMouseVector.x;
        float menuY = menuMouseVector.y;
        return (menuX > table.getX() && menuX < table.getX() + table.getWidth() && menuY > table.getY() && menuY < table.getY() + table.getHeight());
    }
}
