package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

import java.util.List;

public class SpeedItem extends Item {
    public SpeedItem(int xUnit, int yUnit, Image img, List<Entity> stillObjects) {
        super(xUnit, yUnit, img, stillObjects);
    }

    @Override
    public void applyEffect(Bomber player) {
        player.increaseSpeed(this);
    }
}
