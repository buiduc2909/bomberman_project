package uet.oop.bomberman.entities.items;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Item;

import java.util.List;

public class Bombitem extends Item {
    public Bombitem(int xUnit, int yUnit, Image img, List<Entity> stillObjects) {
        super(xUnit, yUnit, img, stillObjects);
    }

    @Override
    public void applyEffect(Bomber player) {
        player.increaseBombLimit(this); // Giả định Bomber có phương thức này
    }
}
