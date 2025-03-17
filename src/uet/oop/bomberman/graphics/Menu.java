package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class Menu {
    private int selectedOption = 0;  // 0: Start, 1: Exit
    private final String[] options = {"Start Game", "Exit"};

    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, BombermanGame.WIDTH * Sprite.SCALED_SIZE, BombermanGame.HEIGHT * Sprite.SCALED_SIZE);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font(30));

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.YELLOW);
                gc.fillText("> " + options[i] + " <", 150, 200 + i * 50);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillText(options[i], 150, 200 + i * 50);
            }
        }
    }

    public void handleInput(KeyCode key) {
        if (key == KeyCode.UP) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
        } else if (key == KeyCode.DOWN) {
            selectedOption = (selectedOption + 1) % options.length;
        } else if (key == KeyCode.ENTER) {
            if (selectedOption == 0) {
                BombermanGame.setState(BombermanGame.GameState.PLAYING);
            } else if (selectedOption == 1) {
                System.exit(0);
            }
        }
    }
}

