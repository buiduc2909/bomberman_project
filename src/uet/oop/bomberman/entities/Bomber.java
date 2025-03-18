package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import java.util.List;

public class Bomber extends Entity {
    private int speed = 2;
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private List<Entity> enemies;
    private List<Entity> stillObjects;
    private List<Entity> bombs;
    private int animationStep = 0;
    private int hp;
    private boolean dead;
    private long deathStartTime = 0;
    private boolean invincible = false;
    private long invincibleStartTime = 0;

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public List<Entity> getStillObjects() {
        return stillObjects;
    }

    public void setStillObjects(List<Entity> stillObjects) {
        this.stillObjects = stillObjects;
    }

    public List<Entity> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Entity> enemies) {
        this.enemies = enemies;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Bomber(int x, int y, Image img, List<Entity> stillObjects, List<Entity> bombs, List<Entity> enemies) {
        super(x, y, img);
        this.stillObjects = stillObjects;
        this.enemies = enemies;
        this.bombs = bombs;
        this.dead = false;
        this.hp = 3;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveUp() {
        if(!dead) {
            int newY = y - Sprite.SCALED_SIZE;
            if (canMove(x, newY)) {
                movingUp = true;
                movingDown = movingLeft = movingRight = false;
                y = newY;
            }
        }
    }

    public void moveDown() {
        if(!dead) {
            int newY = y + Sprite.SCALED_SIZE;
            if (canMove(x, newY)) {
                movingDown = true;
                movingUp = movingLeft = movingRight = false;
                y = newY;
            }
        }
    }

    public void moveLeft() {
        if(!dead) {
            int newX = x - Sprite.SCALED_SIZE;
            if (canMove(newX, y)) {
                movingLeft = true;
                movingUp = movingDown = movingRight = false;
                x = newX;
            }
        }
    }

    public void moveRight() {
        if(!dead) {
            int newX = x + Sprite.SCALED_SIZE;
            if (canMove(newX, y)) {
                movingRight = true;
                movingUp = movingDown = movingLeft = false;
                x = newX;
            }
        }
    }

    public void stopMove() {
        movingUp = movingDown = movingLeft = movingRight = false;
    }

    private boolean canMove(int newX, int newY) {
        for (Entity entity : stillObjects) {
            if (entity instanceof Wall || entity instanceof Brick) {
                if (entity.getX() == newX && entity.getY() == newY) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeBomb() {
        if(dead) return;
        int bombX = this.x / Sprite.SCALED_SIZE;
        int bombY = this.y / Sprite.SCALED_SIZE;

        for (Entity bomb : bombs) {
            if (bomb.getX() / Sprite.SCALED_SIZE == bombX && bomb.getY() / Sprite.SCALED_SIZE == bombY) {
                return;
            }
        }

        Bomb bomb = new Bomb(bombX, bombY, stillObjects, bombs);
        bombs.add(bomb);
    }

    public List<Entity> getBombs() {
        return bombs;
    }

    public void setBombs(List<Entity> bombs) {
        this.bombs = bombs;
    }

    public void takeDamage() {
        if (dead || invincible) return; // Không nhận sát thương nếu đã chết hoặc đang bất tử
        if(dead) return;
        hp--;
        if (hp <= 0) {
            die();
            stopMove();
        }
        else {
            // Kích hoạt chế độ bất tử trong 1 giây
            invincible = true;
            invincibleStartTime = System.currentTimeMillis();
        }
    }

    private void checkCollisionWithEnemies() {
        for (Entity enemy : enemies) {
            if (enemy.getX() == this.x && enemy.getY() == this.y) {
                takeDamage();
            }
        }
    }

    public void die() {
        System.out.println("Bomber has died!");
        dead = true;
        deathStartTime = System.currentTimeMillis();
        // Thêm logic xử lý khi người chơi chết, ví dụ: hiển thị màn hình game over
    }
    @Override
    public void update() {
        if (dead) {
            long elapsedTime = System.currentTimeMillis() - deathStartTime;
            if (elapsedTime >= 600) return; // Kết thúc hoạt ảnh sau 0.6 giây
            if (elapsedTime >= 400) {
                img = Sprite.player_dead3.getFxImage();
            } else if (elapsedTime >= 200) {
                img = Sprite.player_dead2.getFxImage();
            } else {
                img = Sprite.player_dead1.getFxImage();
            }
            return;
        }
        if (invincible) {
            if (System.currentTimeMillis() - invincibleStartTime > 1000) {
                invincible = false;
            }
        }

        // Hiệu ứng nhấp nháy (ẩn/hiện nhân vật)
        if (invincible && (System.currentTimeMillis() / 100) % 2 == 0) {
            img = null; // Ẩn nhân vật
        }
        else {
        int newX = x, newY = y;

        if (movingUp) {
            newY -= speed;
            if (canMove(x, newY)) {
                animationStep = (animationStep + 1) % 3;
                img = (animationStep == 0) ? Sprite.player_up.getFxImage() :
                        (animationStep == 1) ? Sprite.player_up_1.getFxImage() :
                                Sprite.player_up_2.getFxImage();

            }
        }
        if (movingDown) {
            newY += speed;
            if (canMove(x, newY)) {
                animationStep = (animationStep + 1) % 3;
                img = (animationStep == 0) ? Sprite.player_down.getFxImage() :
                        (animationStep == 1) ? Sprite.player_down_1.getFxImage() :
                                Sprite.player_down_2.getFxImage();

            }
        }
        if (movingLeft) {
            newX -= speed;
            if (canMove(newX, y)) {
                animationStep = (animationStep + 1) % 3;
                img = (animationStep == 0) ? Sprite.player_left.getFxImage() :
                        (animationStep == 1) ? Sprite.player_left_1.getFxImage() :
                                Sprite.player_left_2.getFxImage();

            }
        }
        if (movingRight) {
            newX += speed;
            if (canMove(newX, y)) {
                animationStep = (animationStep + 1) % 3;
                img = (animationStep == 0) ? Sprite.player_right.getFxImage() :
                        (animationStep == 1) ? Sprite.player_right_1.getFxImage() :
                                Sprite.player_right_2.getFxImage();

            }
        }
        checkCollisionWithEnemies();
        }
    }
}
