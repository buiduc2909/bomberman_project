package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class SelectLevelMenu extends Menu {

    public SelectLevelMenu(BombermanGame game) {
        super(game, new String[]{"Level 1", "Level 2", "Level 3","Back"});
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

    @Override
    public void select() {
        switch (selectedOption) {
            case 0:
                game.setLevel(1);
                game.setCurrentState(BombermanGame.gameState.PLAYING);
                break;
            case 1:
                game.setLevel(2);
                game.setCurrentState(BombermanGame.gameState.PLAYING);
                break;
            case 2:
                game.setLevel(3);
                game.setCurrentState(BombermanGame.gameState.PLAYING);
                break;
            case 3:
                game.setCurrentState(BombermanGame.gameState.MENU);
                break;
        }
    }
}
