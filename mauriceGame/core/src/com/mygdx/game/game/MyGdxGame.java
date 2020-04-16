package com.mygdx.game.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.gamestates.GameStateManager;
import com.mygdx.game.gamestates.MainMenu;
import com.mygdx.game.preferences.Preffies;


public class MyGdxGame extends ApplicationAdapter {
    public static final float STEP = 1 / 60f;
    public static final int worldWidth = 1600;
    public static final int worldHeight = 900;
    //1600 , 900
    public static Skin uiSkin;

    private float accum;
    private GameStateManager gsm;

    @Override
    public void create() {

        uiSkin = new Skin(Gdx.files.internal("rainbow-ui.json"));
        updateGraphics(Gdx.app.getPreferences(Preffies.FILE_NAME));

        gsm = new GameStateManager();
        gsm.pushState(new MainMenu(gsm));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gsm.resize(width, height);
    }

    public static void updateGraphics(final Preferences prefs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int width = prefs.getInteger(Preffies.WIDTH_KEY, Preffies.BASE_RESOLUTION.width);
                final int height = prefs.getInteger(Preffies.HEIGHT_KEY, Preffies.BASE_RESOLUTION.height);
                final boolean fullscreen = prefs.getBoolean(Preffies.FULLSCREEN_KEY, Preffies.BASE_FULLSCREEN);
                prefs.flush();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (fullscreen) {
                            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                        } else {
                            Gdx.graphics.setWindowedMode(width, height);
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void render() {
        accum += Gdx.graphics.getDeltaTime();
        while (accum >= STEP) {
            accum -= STEP;
            gsm.update(STEP);
        }
        gsm.render();
    }

    @Override
    public void dispose() {
        gsm.dispose();
    }

}
