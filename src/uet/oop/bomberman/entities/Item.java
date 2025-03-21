package uet.oop.bomberman.entities;

import javafx.application.Platform;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public abstract class Item extends Entity {
    protected boolean pickedUp;
    private int oldX, oldY;
    private final List<Entity> stillObjects;
    public Item(int xUnit, int yUnit, Image img, List<Entity> stillObjects) {
        super(xUnit, yUnit, img);
        this.stillObjects = stillObjects;
    }

    /**
     * Áp dụng hiệu ứng của item lên người chơi
     */
    public abstract void applyEffect(Bomber player);

    /**
     * Khi Bomber nhặt item, hiệu ứng được kích hoạt và item bị ẩn đi
     */

    public void pickUp(Bomber player) {
        if (!pickedUp) {
            applyEffect(player);
            pickedUp = true;
            oldX = x;
            oldY = y;
            Platform.runLater(() -> {
                stillObjects.add(new Grass(oldX, oldY, Sprite.grass.getFxImage()));
            });
        }
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    @Override
    public void update() {
        if(!pickedUp) {
            img = Sprite.grass.getFxImage();
        }
        // Item không có logic cập nhật liên tục, có thể mở rộng nếu cần
    }
}
