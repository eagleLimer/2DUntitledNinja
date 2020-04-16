package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.preferences.Preffies;
import com.mygdx.game.preferences.Resolution;

import java.util.Arrays;

public class OptionsState extends GameState {

    private SpriteBatch batch;
    private Stage stage;

    private OrthographicCamera camera;

    public OptionsState(final StateChangeListener stateChangeListener) {
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
        final SelectBox<Resolution> resBox = new SelectBox<Resolution>(MyGdxGame.uiSkin);
        final Preferences prefs = Gdx.app.getPreferences(Preffies.FILE_NAME);
        int width = prefs.getInteger(Preffies.WIDTH_KEY, Preffies.BASE_RESOLUTION.width);
        int height = prefs.getInteger(Preffies.HEIGHT_KEY, Preffies.BASE_RESOLUTION.height);
        resBox.setItems(Preffies.RESOLUTIONS);
        //set chosen item to current resolution
        int chosenIndex = Arrays.asList(Preffies.RESOLUTIONS).indexOf(new Resolution(width, height));
        if (chosenIndex == -1) chosenIndex = 0;
        resBox.setSelectedIndex(chosenIndex);

        final CheckBox fullscreenBox = new CheckBox("Fullscreen", MyGdxGame.uiSkin);
        TextButton applyButton = new TextButton("Apply", MyGdxGame.uiSkin);
        TextButton backButton = new TextButton("Back", MyGdxGame.uiSkin);

        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean choseFullscreen = fullscreenBox.isChecked();
                prefs.putBoolean(Preffies.FULLSCREEN_KEY, choseFullscreen);
                Resolution chosenRes = resBox.getSelected();
                prefs.putInteger(Preffies.WIDTH_KEY, chosenRes.width);
                prefs.putInteger(Preffies.HEIGHT_KEY, chosenRes.height);
                MyGdxGame.updateGraphics(prefs);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.popState();
            }
        });

        //Add buttons to table
        mainTable.padTop(100);
        mainTable.add(resBox);
        mainTable.add(fullscreenBox);
        mainTable.row();
        mainTable.add(applyButton);
        mainTable.row();
        mainTable.add(backButton);

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
