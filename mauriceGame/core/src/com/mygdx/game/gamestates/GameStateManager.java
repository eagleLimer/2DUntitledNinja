package com.mygdx.game.gamestates;

import com.badlogic.gdx.Gdx;

import java.util.Stack;

public class GameStateManager implements StateChangeListener{
    private Stack<GameState> gameStates = new Stack<GameState>();

    public void render() {
        if(gameStates.isEmpty())return;
        gameStates.peek().render();
    }

    public void update(float step) {
        if(gameStates.isEmpty())return;
        gameStates.peek().update(step);
    }

    @Override
    public void popState() {
        GameState gameState = gameStates.pop();
        gameState.dispose();
        if(!gameStates.isEmpty()) {
            gameStates.peek().show();
        }else{
            Gdx.app.exit();
        }
    }

    @Override
    public void pushState(GameState state) {
        if(!gameStates.isEmpty()) {
            gameStates.peek().hide();
        }
        gameStates.push(state);
        state.show();
    }

    public void dispose() {
        while(!gameStates.isEmpty()){
            popState();
        }
    }

    public void resize(int width, int height) {
        for (GameState g:gameStates) {
            g.resize(width,height);
        }
    }
}
