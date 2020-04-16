package com.mygdx.game.gamestates;


import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class GameState {
    protected final StateChangeListener stateChangeListener;
    protected Viewport viewport;

    private GameState() {
        stateChangeListener = new GameStateManager();
    }

    public GameState(StateChangeListener stateListener) {
        this.stateChangeListener = stateListener;
    }

    public abstract void show();

    public abstract void render();

    public abstract void update(float step);

    public abstract void hide();

    public abstract void dispose();

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
