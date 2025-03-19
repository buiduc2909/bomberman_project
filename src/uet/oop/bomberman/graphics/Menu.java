package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;

public class Menu {
    protected int selectedOption = 0;  // Option đang được chọn
    protected String[] options;  // Các tùy chọn của menu
    protected Image background;  // Ảnh nền menu
    protected BombermanGame game;
    private MediaPlayer mediaPlayer;

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

        // Tải nhạc nền
        try {
            String musicPath = getClass().getResource("/sound/01 Title Screen.wav").toExternalForm();
            Media sound = new Media(musicPath);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Lỗi tải nhạc nền: " + e.getMessage());
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(background, 0, 0, game.WIDTH * Sprite.SCALED_SIZE, game.HEIGHT * Sprite.SCALED_SIZE);
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

    public void stopMusic() {
        mediaPlayer.stop();
    }
}
