package uet.oop.bomberman.entities;

import javafx.application.Platform;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import java.util.*;

public class Balloon extends Ghost {
    private int direction = new Random().nextInt(4);
    private long lastMoveTime = System.currentTimeMillis();
    private static final long MOVE_DELAY = 500;
    private boolean isAlive = true;
    private Bomber bomber;
    private int animationStep = 0;

    public Balloon(int x, int y, Image img, Bomber bomber) {
        super(x, y, img, bomber.getStillObjects(),bomber.getBombs(),bomber.getEnemies());
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
            case 0: dy = -Sprite.SCALED_SIZE; img = getBalloomLeftSprite(); break; // Lên
            case 1: dy = Sprite.SCALED_SIZE; img = getBalloomRightSprite(); break; // Xuống
            case 2: dx = -Sprite.SCALED_SIZE; img = getBalloomLeftSprite(); break; // Trái
            case 3: dx = Sprite.SCALED_SIZE; img = getBalloomRightSprite(); break; // Phải
        }

        // Nếu không thể đi tiếp, chọn hướng khác
        if (!canMove(x + dx, y + dy)) {
            direction = getNewDirection();
        } else {
            x += dx;
            y += dy;
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

    public void checkBombCollision() {
        if (!isAlive) return;

        int balloonTileX = this.x / Sprite.SCALED_SIZE;
        int balloonTileY = this.y / Sprite.SCALED_SIZE;

        for (Entity entity : bombs) {
            Bomb bomb = (Bomb) entity;
            int bombTileX = bomb.getX() / Sprite.SCALED_SIZE;
            int bombTileY = bomb.getY() / Sprite.SCALED_SIZE;
            int range = bomb.getBlastRange();

            if (bomb.isExploded()) {
                if (isInBlastRange(balloonTileX, balloonTileY, bombTileX, bombTileY, range)) {
                    die();
                    break;
                }
            }
        }
    }

    private boolean isInBlastRange(int x, int y, int bombX, int bombY, int range) {
        for (int i = 0; i <= range; i++) {
            if ((x == bombX && (y == bombY + i || y == bombY - i)) ||  // Kiểm tra dọc
                    (y == bombY && (x == bombX + i || x == bombX - i))) {  // Kiểm tra ngang
                return true;
            }
        }
        return false;
    }

    public void die() {
        this.isAlive = false;
        this.img = Sprite.balloom_dead.getFxImage();
        System.out.println("💀 Balloon đã chết do dính bom!");

        // Xóa khỏi danh sách sau 2.5 giây
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> enemies.remove(Balloon.this));
            }
        }, 2500);
    }

    private int getNewDirection() {
        List<Integer> possibleDirections = new ArrayList<>();
        if (canMove(x, y - Sprite.SCALED_SIZE)) possibleDirections.add(0); // Lên
        if (canMove(x, y + Sprite.SCALED_SIZE)) possibleDirections.add(1); // Xuống
        if (canMove(x - Sprite.SCALED_SIZE, y)) possibleDirections.add(2); // Trái
        if (canMove(x + Sprite.SCALED_SIZE, y)) possibleDirections.add(3); // Phải

        if (possibleDirections.isEmpty()) {
            return direction; // Nếu không có hướng nào hợp lệ, giữ nguyên hướng cũ
        }

        return possibleDirections.get(new Random().nextInt(possibleDirections.size()));
    }
}
