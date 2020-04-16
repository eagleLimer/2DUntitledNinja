package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.game.MyGdxGame;

import java.io.File;


public class EditMenu extends GameState {
    private SpriteBatch batch;
    private Stage stage;
    private OrthographicCamera camera;
    private static final int MAX_HEIGHT = 500;
    private static final int MAX_WIDTH = 500;
    private static final String FILE_PATH = "levelFiles/";

    public EditMenu(final StateChangeListener stateChangeListener) {
        super(stateChangeListener);
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(MyGdxGame.worldWidth, MyGdxGame.worldHeight, camera);
        viewport.apply();
        stage = new Stage(viewport, batch);

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();


        //Create Table
        Table rightTable = new Table();
        Table leftTable = new Table();

        //Set table to fill stage
        rightTable.setFillParent(true);
        leftTable.setFillParent(true);
        //Set alignment of contents in the table.
        leftTable.left();
        rightTable.right();


        Label widthLabel = new Label("Enter width: ", MyGdxGame.uiSkin);
        widthLabel.getStyle().fontColor = Color.WHITE;
        Label heightLabel = new Label("Enter height: ", MyGdxGame.uiSkin);
        Label fileLocLabel = new Label("Enter file name: ", MyGdxGame.uiSkin);
        Label fileLocLabel2 = new Label("Enter file name: ", MyGdxGame.uiSkin);
        TextButton newLevelButton = new TextButton("New level", MyGdxGame.uiSkin);
        final TextButton loadLevelButton = new TextButton("Load level", MyGdxGame.uiSkin);
        final TextButton backButton = new TextButton("Back", MyGdxGame.uiSkin);
        final TextField enterMapWidth = new TextField("", MyGdxGame.uiSkin);
        final TextField enterMapHeight = new TextField("", MyGdxGame.uiSkin);
        final TextField enterNewFileName = new TextField("", MyGdxGame.uiSkin);
        final TextField enterFileName = new TextField("", MyGdxGame.uiSkin);


        newLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enterMapWidth.getText() != null && enterMapHeight.getText() != null && enterNewFileName.getText() != null) {
                    try {
                        int enteredWidth = Integer.parseInt(enterMapWidth.getText());
                        int enteredHeight = Integer.parseInt(enterMapHeight.getText());
                        if (enteredWidth > 0 && enteredWidth <= MAX_WIDTH && enteredHeight > 0 && enteredHeight <= MAX_HEIGHT) {
                            stateChangeListener.pushState(new EditState(stateChangeListener, FILE_PATH + enterNewFileName.getText(),
                                    enteredWidth, enteredHeight));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    //todo: Please add message to tell user to enter information!
                }
            }
        });
        loadLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(enterFileName.getText() != null && Gdx.files.local(FILE_PATH + enterFileName.getText()).exists()){
                    stateChangeListener.pushState(new EditState(stateChangeListener, FILE_PATH + enterFileName.getText()));
                }else{
                    //todo: please add no such file message here also!
                }
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateChangeListener.popState();
            }
        });

        rightTable.padRight(100);
        leftTable.padLeft(100);

        rightTable.add(widthLabel).width(100);
        rightTable.add(enterMapWidth);
        rightTable.row();
        rightTable.add(heightLabel).width(100);
        rightTable.add(enterMapHeight);
        rightTable.row();
        rightTable.add(fileLocLabel).width(100);
        rightTable.add(enterNewFileName);
        rightTable.row();
        rightTable.add();
        rightTable.add(newLevelButton);
        rightTable.row();
        rightTable.add();
        rightTable.add(backButton);


        leftTable.padTop(100);
        leftTable.add(fileLocLabel2).width(100);
        leftTable.add(enterFileName);
        leftTable.row();
        leftTable.add();
        leftTable.add(loadLevelButton);

        stage.addActor(rightTable);
        stage.addActor(leftTable);
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
        stage.dispose();
        batch.dispose();
    }
}
