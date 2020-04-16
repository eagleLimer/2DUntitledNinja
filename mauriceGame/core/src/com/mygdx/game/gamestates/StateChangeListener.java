package com.mygdx.game.gamestates;

public interface StateChangeListener {
    public void popState();

    public void pushState(GameState state);
}
