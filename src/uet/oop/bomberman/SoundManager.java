package uet.oop.bomberman;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class SoundManager {
    private static SoundManager instance; // Singleton Instance
    private MediaPlayer mediaPlayer;
    private String filePath;

    private SoundManager() { } // Private constructor để ngăn tạo nhiều thực thể

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void play() {
        stop(); // Luôn dừng nhạc trước khi phát mới

        if (filePath == null) {
            System.out.println("⚠️ Không có filePath!");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("❌ File không tồn tại: " + filePath);
            return;
        }

        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.2);
            mediaPlayer.setOnEndOfMedia(this::stop);
            mediaPlayer.play();

            System.out.println("🎵 Đang phát: " + filePath);
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi tạo Media: " + e.getMessage());
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            System.out.println("⏹ Dừng nhạc.");
            mediaPlayer.stop();
        }
    }
}
