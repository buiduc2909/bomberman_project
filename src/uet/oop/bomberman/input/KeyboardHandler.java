package uet.oop.bomberman.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.graphics.Menu;

public class KeyboardHandler {
    private final Bomber bomber;
    private final Menu menu;

    public KeyboardHandler(Scene scene, Bomber bomber, Menu menu) {
        this.bomber = bomber;
        this.menu = menu;

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> handleKeyRelease(event.getCode()));
    }

    private void handleKeyPress(KeyCode key) {
        if (BombermanGame.getCurrentState() == BombermanGame.gameState.MENU || BombermanGame.getCurrentState() == BombermanGame.gameState.OVER) {
            handleMenuInput(key);
        } else if (BombermanGame.getCurrentState() == BombermanGame.gameState.PLAYING) {
            if(key == KeyCode.ESCAPE) {
                BombermanGame.setCurrentState(BombermanGame.gameState.MENU);
            }
            else {
                handleGameInput(key);
            }
        }
    }

    private void handleMenuInput(KeyCode key) {
        switch (key) {
            case W:
            case UP:
                menu.moveUp();
                break;
            case S:
            case DOWN:
                menu.moveDown();
                break;
            case ENTER:
                menu.select();
                break;
            default:
                break;
        }
    }

    private void handleGameInput(KeyCode key) {
        switch (key) {
            case W:
            case UP:
                bomber.moveUp();
                break;
            case S:
            case DOWN:
                bomber.moveDown();
                break;
            case A:
            case LEFT:
                bomber.moveLeft();
                break;
            case D:
            case RIGHT:
                bomber.moveRight();
                break;
            case SPACE:
                bomber.placeBomb();
                break;
            default:
                break;
        }
    }

    private void handleKeyRelease(KeyCode key) {
        if (BombermanGame.getCurrentState() == BombermanGame.gameState.PLAYING) {
            bomber.stopMove();
        }
    }
}
