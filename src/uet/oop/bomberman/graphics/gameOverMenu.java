package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class gameOverMenu extends Menu {
    private double alpha = 0;  // Độ trong suốt của chữ "Game Over"
    private double fadeOpacity = 0.0; // Khởi tạo với 0

    public gameOverMenu() {
        super();
        this.options = new String[]{"Retry", "Main Menu"};
    }

    @Override
    public void render(GraphicsContext gc) {
        fadeOpacity = Math.min(fadeOpacity + 0.02, 1.0); // Tăng từ từ nhưng không quá 1.0

        // Vẽ ảnh nền
        gc.drawImage(background, 0, 0, BombermanGame.WIDTH * Sprite.SCALED_SIZE, BombermanGame.HEIGHT * Sprite.SCALED_SIZE);

        // Hiệu ứng chữ "Game Over" hiện dần
        if (alpha < 1) {
            alpha += 0.02;
        }
        gc.setFill(Color.color(1, 0, 0, fadeOpacity));  // Đỏ, mờ dần hiện ra
        gc.setFont(new Font(50));
        gc.fillText("Game Over", 200, 150);

        gc.setFont(new Font(30));
        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.YELLOW);
                gc.fillText("> " + options[i] + " <", 180, 200 + i * 50);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillText(options[i], 180, 200 + i * 50);
            }
        }
    }

    @Override
    public void select() {
        if (selectedOption == 0) {  // "Retry"
            BombermanGame.restartGame();
        } else if (selectedOption == 1) {  // "Main Menu"
            BombermanGame.setCurrentState(BombermanGame.gameState.MENU);
        }
    }
}
