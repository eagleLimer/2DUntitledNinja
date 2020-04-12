package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.game.MyGdxGame;

public class PlayMenu extends GameState {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage stage;
    private PlayState playState;
    public PlayMenu(final StateChangeListener stateChangeListener, final PlayState playState) {
        super(stateChangeListener);
        this.playState = playState;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        stage = new Stage(viewport,batch);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton resumeButton = new TextButton("Resume Game", MyGdxGame.uiSkin);
        TextButton saveGameButton = new TextButton("Save Game", MyGdxGame.uiSkin);
        TextButton mainMenuButton = new TextButton("Exit to MainMenu", MyGdxGame.uiSkin);

        //Add listeners to buttons
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.popState();
            }
        });
        saveGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //create gameData and save progress there.
            }
        });
        mainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //maybe just call Gdx.app.exit(); instead :shrug:
                stateChangeListener.popState();
                stateChangeListener.popState();
            }
        });

        //Add buttons to table
        mainTable.padTop(100);
        mainTable.add(resumeButton);
        mainTable.row();
        mainTable.add(saveGameButton);
        mainTable.row();
        mainTable.add(mainMenuButton);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playState.menuRender();
        stage.act();
        stage.draw();
    }

    @Override
    public void update(float step) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
