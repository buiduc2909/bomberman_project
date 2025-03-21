package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class Portal extends Entity {
    private final List<Entity> enemies; // Danh sách kẻ địch
    private final Bomber bomber;
    private final BombermanGame game;

    public Portal(int x, int y, Image img, List<Entity> enemies, Bomber bomber, BombermanGame game) {
        super(x, y, img);
        this.enemies = enemies;
        this.bomber = bomber;
        this.game = game;
    }

    @Override
    public void update() {
        if (enemies.isEmpty() && isCollidingWithBomber()) {
            game.setLevel(game.getCurrentLevel() + 1); // Tăng cấp độ khi qua Portal
        }
    }

    private boolean isCollidingWithBomber() {
        return bomber.getX() / Sprite.SCALED_SIZE == x / Sprite.SCALED_SIZE &&
                bomber.getY() / Sprite.SCALED_SIZE == y / Sprite.SCALED_SIZE;
    }
}
