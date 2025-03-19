package uet.oop.bomberman.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.graphics.Menu;
import uet.oop.bomberman.graphics.SelectLevelMenu;
import uet.oop.bomberman.graphics.gameOverMenu;
import uet.oop.bomberman.graphics.mainMenu;

public class KeyboardHandler {
    private final Bomber bomber;
    private final mainMenu menu;
    private final gameOverMenu overMenu;
    private final SelectLevelMenu selectLevelMenu;
    private final BombermanGame game;

    public KeyboardHandler(Scene scene, Bomber bomber, mainMenu menu,BombermanGame game) {
        this.bomber = bomber;
        this.menu = menu;
        this.overMenu = menu.getGameoverMenu();
        this.selectLevelMenu = menu.getLevelMenu();
        this.game = game;

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> handleKeyRelease(event.getCode()));
    }

    private void handleKeyPress(KeyCode key) {
        if (game.getCurrentState() == BombermanGame.gameState.MENU || game.getCurrentState() == BombermanGame.gameState.OVER || game.getCurrentState() == BombermanGame.gameState.SELECTING_LEVEL) {
            handleMenuInput(key);
        } else if (game.getCurrentState() == BombermanGame.gameState.PLAYING) {
            if(key == KeyCode.ESCAPE) {
                game.setCurrentState(BombermanGame.gameState.MENU);
            }
            else {
                handleGameInput(key);
            }
        }
    }

    private void handleMenuInput(KeyCode key) {
        if (game.getCurrentState() == BombermanGame.gameState.MENU) {
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
        } else if (game.getCurrentState() == BombermanGame.gameState.OVER) {
            // Xử lý sự kiện cho game over menu
            switch (key) {
                case W:
                case UP:
                    overMenu.moveUp();
                    break;
                case S:
                case DOWN:
                    overMenu.moveDown();
                    break;
                case ENTER:
                    overMenu.select();
                    break;
                default:
                    break;
            }
        } else if (game.getCurrentState() == BombermanGame.gameState.SELECTING_LEVEL) {
            // Xử lý sự kiện cho select level menu
            switch (key) {
                case W:
                case UP:
                    selectLevelMenu.moveUp();
                    break;
                case S:
                case DOWN:
                    selectLevelMenu.moveDown();
                    break;
                case ENTER:
                    selectLevelMenu.select();
                    break;
                default:
                    break;
            }
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
        if (game.getCurrentState() == BombermanGame.gameState.PLAYING) {
            bomber.stopMove();
        }
    }
}
