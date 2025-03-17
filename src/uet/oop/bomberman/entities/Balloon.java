package uet.oop.bomberman.entities;

import javafx.application.Platform;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Balloon extends Ghost {
    private int direction = new Random().nextInt(4);
    private long lastMoveTime = System.currentTimeMillis();
    private static final long MOVE_DELAY = 500;
    private boolean isAlive = true;
    private Bomber bomber;
    private int animationStep = 0;

    public Balloon(int x, int y, Image img, Bomber bomber) {
        super(x, y, img, bomber.getStillObjects(),bomber.getBombs(),bomber.getEnemies());
        this.bombs = bombs;
    }

    @Override
    public void move() {
        if (!isAlive) return;

        long now = System.currentTimeMillis();
        if (now - lastMoveTime < MOVE_DELAY) {
            return;
        }
        lastMoveTime = now;

        int dx = 0, dy = 0;
        switch (direction) {
            case 0: dy = -Sprite.SCALED_SIZE; img = getBalloomLeftSprite(); break;
            case 1: dy = Sprite.SCALED_SIZE; img = getBalloomRightSprite(); break;
            case 2: dx = -Sprite.SCALED_SIZE; img = getBalloomLeftSprite(); break;
            case 3: dx = Sprite.SCALED_SIZE; img = getBalloomRightSprite(); break;
        }

        if (canMove(x + dx, y + dy)) {
            x += dx;
            y += dy;
        } else {
            direction = new Random().nextInt(4);
        }
        animationStep = (animationStep + 1) % 3;
    }

    private Image getBalloomLeftSprite() {
        switch (animationStep) {
            case 0: return Sprite.balloom_left1.getFxImage();
            case 1: return Sprite.balloom_left2.getFxImage();
            case 2: return Sprite.balloom_left3.getFxImage();
            default: return Sprite.balloom_left1.getFxImage();
        }
    }

    private Image getBalloomRightSprite() {
        switch (animationStep) {
            case 0: return Sprite.balloom_right1.getFxImage();
            case 1: return Sprite.balloom_right2.getFxImage();
            case 2: return Sprite.balloom_right3.getFxImage();
            default: return Sprite.balloom_right1.getFxImage();
        }
    }

    private boolean canMove(int newX, int newY) {
        int gridX = newX / Sprite.SCALED_SIZE;
        int gridY = newY / Sprite.SCALED_SIZE;

        for (Entity entity : stillObjects) {
            int entityGridX = entity.getX() / Sprite.SCALED_SIZE;
            int entityGridY = entity.getY() / Sprite.SCALED_SIZE;

            if (entityGridX == gridX && entityGridY == gridY) {
                return !(entity instanceof Wall || entity instanceof Brick);
            }
        }
        return true;
    }

    public void update() {
        if (!isAlive) {
            this.img = Sprite.balloom_dead.getFxImage();
        }
        move();
        checkBombCollision();
    }

    private void checkBombCollision() {
        if (!isAlive) return;

        int balloonTileX = this.x / Sprite.SCALED_SIZE;
        int balloonTileY = this.y / Sprite.SCALED_SIZE;

        for (Entity entity : bombs) {
            Bomb bomb = (Bomb) entity;
            int bombTileX = bomb.getX() / Sprite.SCALED_SIZE;
            int bombTileY = bomb.getY() / Sprite.SCALED_SIZE;
            int range = bomb.getBlastRange();

            if (bomb.isExploded()) {
                System.out.println("Checking explosion: Balloon at " + balloonTileX + "," + balloonTileY +
                        " | Bomb at " + bombTileX + "," + bombTileY + " | Range: " + range);
                if (isInBlastRange(balloonTileX, balloonTileY, bombTileX, bombTileY, range)) {
                    die();
                    break;
                }
            }
        }
    }

    private boolean isInBlastRange(int x, int y, int bombX, int bombY, int range) {
        if (x == bombX && Math.abs(y - bombY) <= range) return true;
        if (y == bombY && Math.abs(x - bombX) <= range) return true;
        return false;
    }

    public void die() {
        this.isAlive = false;
        this.img = Sprite.balloom_dead.getFxImage();
        System.out.println("💀 Balloon đã chết do dính bom!");

        // Xóa khỏi danh sách sau 1 giây
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> enemies.remove(Balloon.this));
            }
        }, 2500);
    }
}
