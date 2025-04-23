package uet.oop.bomberman;

import javafx.scene.media.AudioClip;
import java.io.File;

public class SFXManager {

    public static void playSound(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("❌ File không tồn tại: " + filePath);
                return;
            }

            AudioClip sound = new AudioClip(file.toURI().toString());
            sound.setVolume(0.5); // Chỉnh âm lượng tại đây
            sound.play();

            System.out.println("🔊 Phát hiệu ứng âm thanh: " + filePath);
        } catch (Exception e) {
            System.out.println("❌ Lỗi phát hiệu ứng âm thanh: " + e.getMessage());
        }
    }
}

