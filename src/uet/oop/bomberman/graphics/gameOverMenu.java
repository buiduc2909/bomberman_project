package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class gameOverMenu extends Menu {
    private double alpha = 0;  // Độ trong suốt của chữ "Game Over"
    private double fadeOpacity = 0.0; // Khởi tạo với 0

    public gameOverMenu(BombermanGame game) {
        super(game, new String[]{"Retry", "Main Menu"}); // Gọi constructor cha

        try {
            String path = getClass().getResource("/textures/game_over.png").toExternalForm();
            background = new Image(path);  // Tải ảnh nền riêng cho Game Over
        } catch (Exception e) {
            System.out.println("Lỗi tải ảnh nền Game Over: " + e.getMessage());
        }
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

        // Vẽ chữ "Game Over" với độ mờ dần, đảm bảo alpha không vượt quá 1.0
        gc.setFill(Color.color(1, 0, 0, Math.min(alpha, 1.0)));  // Đỏ, mờ dần hiện ra
        gc.setFont(new Font(50));
        gc.fillText("Game Over", 200, 150);

        // Vẽ các tùy chọn của menu
        gc.setFont(new Font(30));
        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.BLUE);  // Màu vàng cho lựa chọn hiện tại
                gc.fillText("> " + options[i] + " <", 300, 200 + i * 50);
            } else {
                gc.setFill(Color.BLACK);  // Màu trắng cho các lựa chọn khác
                gc.fillText(options[i], 300, 200 + i * 50);
            }
        }
    }

    @Override
    public void select() {
        if (selectedOption == 0) {  // "Retry"
            game.restartGame();  // Khởi động lại trò chơi
        } else if (selectedOption == 1) {  // "Main Menu"
            game.setCurrentState(BombermanGame.gameState.MENU);  // Quay lại menu chính
        }
    }
}
