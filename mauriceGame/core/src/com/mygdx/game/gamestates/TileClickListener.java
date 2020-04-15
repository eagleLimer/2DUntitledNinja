package com.mygdx.game.gamestates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * This class is here so that editstate can create multiple buttons in a loop and give each button a unique action.
 */
public class TileClickListener extends ClickListener {
    private int id;
    public TileClickListener(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
}
