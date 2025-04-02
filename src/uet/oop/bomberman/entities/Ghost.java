package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;
import java.util.Random;

public abstract class Ghost extends Entity {
    protected int speed = 1;
    protected boolean alive = true;
    protected Random random = new Random();
    protected List<Entity> stillObjects;
    protected List<Entity> bombs;
    protected List<Entity> enemies;

    public Ghost(int x, int y, Image img, List<Entity> stillObjects, List<Entity> bombs, List<Entity> enemies) {
        super(x, y, img);
        this.stillObjects = stillObjects;
        this.bombs = bombs;
        this.enemies = enemies;
    }


    public abstract void checkBombCollision();
    public abstract void move(); // Phương thức di chuyển riêng của từng quái

    public boolean isAlive() {
        return alive;
    }
    @Override
    public void update() {
        if (alive) {
            move();
            checkBombCollision();
        }
    }

}
