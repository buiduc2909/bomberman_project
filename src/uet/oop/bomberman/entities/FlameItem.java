package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

import java.util.List;

public class FlameItem extends Item {
    public FlameItem(int xUnit, int yUnit, Image img, List<Entity> stillObjects) {
        super(xUnit, yUnit, img,stillObjects);
    }

    @Override
    public void applyEffect(Bomber player) {
        player.increaseExplosionRange(this);
    }
}
