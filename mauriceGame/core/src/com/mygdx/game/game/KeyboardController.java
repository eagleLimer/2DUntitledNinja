package com.mygdx.game.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

public class KeyboardController implements InputProcessor {
    //should be private with getters?
    public boolean left, right, up, down, esc;
    public boolean leftButton, rightButton;
    public boolean zoomIn, zoomOut;
    public boolean jump;
    public boolean explode, implode;

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Keys.A:     // if keycode is the same as Keys.LEFT a.k.a 21
                left = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.D:    // if keycode is the same as Keys.LEFT a.k.a 22
                right = true;   // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.E:
                explode = true;
                keyProcessed = true;
                break;
            case Keys.Q:
                implode = true;
                keyProcessed = true;
                break;
            case Keys.W:       // if keycode is the same as Keys.LEFT a.k.a 19
                up = true;      // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.S:     // if keycode is the same as Keys.LEFT a.k.a 20
                down = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.ESCAPE:     // if keycode is the same as Keys.LEFT a.k.a 20
                esc = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.O:     // if keycode is the same as Keys.LEFT a.k.a 20
                zoomIn = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.P:     // if keycode is the same as Keys.LEFT a.k.a 20
                zoomOut = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.SPACE:
                jump = true;
                keyProcessed = true;
                break;
        }
        return keyProcessed;    //  return our peyProcessed flag
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Keys.A:
                left = false;
                keyProcessed = true;
                break;
            case Keys.D:
                right = false;
                keyProcessed = true;
                break;
            case Keys.E:
                explode = false;
                keyProcessed = true;
                break;
            case Keys.Q:
                implode = false;
                keyProcessed = true;
                break;
            case Keys.W:
                up = false;
                keyProcessed = true;
                break;
            case Keys.S:
                down = false;
                keyProcessed = true;
                break;
            case Keys.ESCAPE:
                esc = false;
                keyProcessed = true;
                break;
            case Keys.O:
                zoomIn = false;
                keyProcessed = true;
                break;
            case Keys.P:
                zoomOut = false;
                keyProcessed = true;
                break;
            case Keys.SPACE:
                jump = false;
                keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Boolean keyProcessed = false;
        switch (button) {
            case Buttons.LEFT:
                leftButton = true;
                keyProcessed = true;
                break;
            case Buttons.RIGHT:
                rightButton = true;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Boolean keyProcessed = false;
        switch (button) {
            case Buttons.LEFT:
                leftButton = false;
                keyProcessed = true;
                break;
            case Buttons.RIGHT:
                rightButton = false;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
