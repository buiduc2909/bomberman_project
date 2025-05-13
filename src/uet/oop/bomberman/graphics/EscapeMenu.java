package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class EscapeMenu extends Menu{

    public EscapeMenu(BombermanGame game) {
        super(game, new String[]{"Resume Game", "Restart Game", "Back to menu", "Exit"});
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(30));

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.BLUE);  // Màu của lựa chọn hiện tại
                gc.fillText("> " + options[i] + " <", 300, 200 + i * 50);
            } else {
                gc.setFill(Color.BLACK);  // Màu của các lựa chọn khác
                gc.fillText(options[i], 300, 200 + i * 50);
            }
        }
    }

    public void moveUp() {
        selectedOption = (selectedOption - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedOption = (selectedOption + 1) % options.length;
    }
    @Override
    public void select() {
        switch (selectedOption) {
            case 0:
                game.resumeGame();
                break;
            case 1:
                game.restartGame();
                break;
            case 2:
                game.setCurrentState(BombermanGame.gameState.MENU);
                break;
            case 3:
                System.exit(0);
                break;
        }
    }
}
