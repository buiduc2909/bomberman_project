package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.SFXManager;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.SpriteSheet;

import java.util.List;

public class Portal extends Entity {
    private final List<Entity> enemies;
    private final Bomber bomber;
    private final BombermanGame game;
    private boolean isOpened = false;

    public Portal(int x, int y, Image img, List<Entity> enemies, Bomber bomber, BombermanGame game) {
        super(x, y);
        this.img = img;
        this.enemies = enemies;
        this.bomber = bomber;
        this.game = game;
    }

    @Override
    public void update() {
        // Khi không còn kẻ địch và portal chưa mở thì đổi hình ảnh
        if (!isOpened && enemies.isEmpty()) {
            Image openedImg = new Sprite(Sprite.DEFAULT_SIZE, 4, 1, SpriteSheet.tiles, 14, 14).getFxImage();
            setImg(openedImg);
            SFXManager.playSound("res/sound/Portal Open.wav");
            isOpened = true;
        }

        if (isOpened && isCollidingWithBomber()) {
            game.setLevel(game.getCurrentLevel() + 1);
        }
    }

    private boolean isCollidingWithBomber() {
        return bomber.getX() / Sprite.SCALED_SIZE == x / Sprite.SCALED_SIZE &&
                bomber.getY() / Sprite.SCALED_SIZE == y / Sprite.SCALED_SIZE;
    }
}
