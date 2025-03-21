package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import uet.oop.bomberman.BombermanGame;

public class Menu {
    protected int selectedOption = 0;  // Option đang được chọn
    protected String[] options;  // Các tùy chọn của menu
    protected Image background;  // Ảnh nền menu
    protected BombermanGame game;


    public Menu(BombermanGame game, String[] options) {
        this.game = game;
        this.options = options;

        // Tải ảnh nền mặc định nếu có
        try {
            String path = getClass().getResource("/textures/main_menu_background.png").toExternalForm();
            background = new Image(path);
        } catch (Exception e) {
            System.out.println("Lỗi tải ảnh nền menu: " + e.getMessage());
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(background, 0, 0, BombermanGame.WIDTH * Sprite.SCALED_SIZE, BombermanGame.HEIGHT * Sprite.SCALED_SIZE);
    }

    public void moveUp() {
        selectedOption = (selectedOption - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedOption = (selectedOption + 1) % options.length;
    }

    public void select() {
        // Chức năng chọn sẽ được định nghĩa trong lớp con
    }

}
