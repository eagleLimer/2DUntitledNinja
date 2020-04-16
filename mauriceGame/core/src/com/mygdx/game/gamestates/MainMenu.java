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

public class MainMenu extends GameState {

    private SpriteBatch batch;
    private Stage stage;
    private OrthographicCamera camera;

    public MainMenu(final StateChangeListener stateChangeListener) {
        super(stateChangeListener);


        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton playButton = new TextButton("Play", MyGdxGame.uiSkin);
        TextButton editLevelButton = new TextButton("Edit Level", MyGdxGame.uiSkin);
        TextButton optionsButton = new TextButton("Options", MyGdxGame.uiSkin);
        TextButton exitButton = new TextButton("Exit", MyGdxGame.uiSkin);

        //Add listeners to buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.pushState(new PlayState(stateChangeListener));
            }
        });
        editLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.pushState(new EditMenu(stateChangeListener));
            }
        });
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.pushState(new OptionsState(stateChangeListener));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //maybe just call Gdx.app.exit(); instead :shrug:
                stateChangeListener.popState();

            }
        });

        //Add buttons to table
        mainTable.padTop(100);
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(editLevelButton);
        mainTable.row();
        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(exitButton);

        //Add table to stage
        stage.addActor(mainTable);
    }


    @Override
    public void show() {
        //Stage should controll input:
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        batch.dispose();
        stage.dispose();
    }


}
