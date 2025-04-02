package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import java.util.List;

public class Bomber extends Entity {
    private int bombLimit = 1;        // Số lượng bom tối đa
    private double speed = 0.25;      // Tốc độ di chuyển
    private int frameCounter = 0;
    private final int frameDelay = 5; // Số frame chờ giữa các lần đổi ảnh
    private final int moveDelay = 30;
    private int moveCounter = 0;
    private boolean isVisible = true;
    private int explosionRange = 1;   // Tầm nổ của bom
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private List<Entity> enemies;
    private List<Entity> stillObjects;
    private List<Entity> bombs;
    private final List<Item> items;
    private int animationStep = 0;
    private int hp;
    private boolean dead;
    private long deathStartTime = 0;
    private boolean invincible = false;
    private long invincibleStartTime = 0;
    private long lastMoveTime = 0;
    private final long MOVE_DELAY = 100; // Độ trễ giữa các bước di chuyển (milliseconds)

    public void setExplosionRange(int explosionRange) {
        this.explosionRange = explosionRange;
    }

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

    public Bomber(int x, int y, Image img, List<Entity> stillObjects, List<Entity> bombs, List<Entity> enemies, List<Item> items) {
        super(x, y, img);
        this.stillObjects = stillObjects;
        this.enemies = enemies;
        this.items = items;
        this.bombs = bombs;
        this.dead = false;
        this.hp = 3;
    }

    public void setImage(Image img){
        this.img = img;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveUp() {
        if (!dead && System.currentTimeMillis() - lastMoveTime >= MOVE_DELAY) {
            int newY = y - Sprite.SCALED_SIZE;
            if (canMove(x, newY)) {
                movingUp = true;
                movingDown = movingLeft = movingRight = false;
                y = newY;
                lastMoveTime = System.currentTimeMillis(); // Cập nhật thời gian di chuyển
            }
        }
    }

    public void moveDown() {
        if (!dead && System.currentTimeMillis() - lastMoveTime >= MOVE_DELAY) {
            int newY = y + Sprite.SCALED_SIZE;
            if (canMove(x, newY)) {
                movingDown = true;
                movingUp = movingLeft = movingRight = false;
                y = newY;
                lastMoveTime = System.currentTimeMillis();
            }
        }
    }

    public void moveLeft() {
        if (!dead && System.currentTimeMillis() - lastMoveTime >= MOVE_DELAY) {
            int newX = x - Sprite.SCALED_SIZE;
            if (canMove(newX, y)) {
                movingLeft = true;
                movingUp = movingDown = movingRight = false;
                x = newX;
                lastMoveTime = System.currentTimeMillis();
            }
        }
    }

    public void moveRight() {
        if (!dead && System.currentTimeMillis() - lastMoveTime >= MOVE_DELAY) {
            int newX = x + Sprite.SCALED_SIZE;
            if (canMove(newX, y)) {
                movingRight = true;
                movingUp = movingDown = movingLeft = false;
                x = newX;
                lastMoveTime = System.currentTimeMillis();
            }
        }
    }

    public void stopMove() {
        movingUp = movingDown = movingLeft = movingRight = false;
        lastMoveTime = 0;
    }

    private boolean canMove(double newX, double newY) {
        double centerX = newX + Sprite.SCALED_SIZE / 2.0;
        double centerY = newY + Sprite.SCALED_SIZE / 2.0;
        for (Entity entity : stillObjects) {
            if (entity instanceof Wall || entity instanceof Brick) {
                double entityLeft = entity.getX();
                double entityRight = entity.getX() + Sprite.SCALED_SIZE;
                double entityTop = entity.getY();
                double entityBottom = entity.getY() + Sprite.SCALED_SIZE;
                if (centerX > entityLeft && centerX < entityRight && centerY > entityTop && centerY < entityBottom) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeBomb() {
        if(dead) return;
        if(bombs.size() >= bombLimit){
            System.out.println("Cannot place more bombs! Limit reached: " + bombLimit);
            return;
        }
        int bombX = this.x / Sprite.SCALED_SIZE;
        int bombY = this.y / Sprite.SCALED_SIZE;

        for (Entity bomb : bombs) {
            if (bomb.getX() / Sprite.SCALED_SIZE == bombX && bomb.getY() / Sprite.SCALED_SIZE == bombY) {
                return;
            }
        }
        System.out.println("bomber blast range: " + explosionRange);
        Bomb bomb = new Bomb(bombX, bombY, stillObjects, bombs, explosionRange);
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

        long elapsedTime = System.currentTimeMillis() - invincibleStartTime;
        if (elapsedTime < 1000) {
            isVisible = (elapsedTime / 100) % 2 == 0; // Đổi trạng thái mỗi 100ms
        }
        else if(elapsedTime >= 1000) {
            invincible = false;
            isVisible = true;
        }

        move();// Di chuyển nhân vật nếu có phím nhấn
        updateImage();
        checkCollisionWithEnemies();
        pickUpItem();

    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private void updateImage() {
        frameCounter++;
        if (frameCounter < frameDelay) return;
        frameCounter = 0; // Reset bộ đếm khi đổi ảnh

        if (movingUp) {
            animationStep = (animationStep + 1) % 3;
            img = (animationStep == 0) ? Sprite.player_up.getFxImage() :
                    (animationStep == 1) ? Sprite.player_up_1.getFxImage() :
                            Sprite.player_up_2.getFxImage();
        } else if (movingDown) {
            animationStep = (animationStep + 1) % 3;
            img = (animationStep == 0) ? Sprite.player_down.getFxImage() :
                    (animationStep == 1) ? Sprite.player_down_1.getFxImage() :
                            Sprite.player_down_2.getFxImage();
        } else if (movingLeft) {
            animationStep = (animationStep + 1) % 3;
            img = (animationStep == 0) ? Sprite.player_left.getFxImage() :
                    (animationStep == 1) ? Sprite.player_left_1.getFxImage() :
                            Sprite.player_left_2.getFxImage();
        } else if (movingRight) {
            animationStep = (animationStep + 1) % 3;
            img = (animationStep == 0) ? Sprite.player_right.getFxImage() :
                    (animationStep == 1) ? Sprite.player_right_1.getFxImage() :
                            Sprite.player_right_2.getFxImage();
        }
    }


    private void move() {
        moveCounter++;
        if (frameCounter < moveDelay) return; // Chỉ cập nhật khi đủ delay
        moveCounter = 0; // Reset bộ đếm

        int newX = x;
        int newY = y;

        if (movingUp) {
            if(canMove(newX, newY)) {
                newY -= speed;
            }
        }
        if (movingDown) {
            if(canMove(newX, newY)) {
                newY += speed;
            }
        }
        if (movingLeft) {
            if(canMove(newX, newY)) {
                newX -= speed;
            }
        }
        if (movingRight) {
            if(canMove(newX, newY)) {
                newX += speed;
            }
        }
    }

    public void pickUpItem() {
        if (items == null || items.isEmpty()) {
            System.out.println("Danh sách items trước khi nhặt: " + items);

            return; // Không có item nào để nhặt
        }
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (this.getX() == item.getX() && this.getY() == item.getY() && !item.pickedUp) {
                item.pickUp(this); // Nhặt vật phẩm
                break;
            }
        }
    }

    private void removeItem(Entity item) {
        items.remove(item);
        System.out.println("Item removed from map.");
    }


    public void increaseBombLimit(Entity item) {
        bombLimit++;
        removeItem(item);
        System.out.println("Bomb limit increased to: " + bombLimit);
    }

    public void increaseSpeed(Entity item) {
        speed += 0.2;// Điều chỉnh mức tăng tốc độ
        removeItem(item);
        System.out.println("Speed increased to: " + speed);
    }

    public void increaseExplosionRange(Entity item) {
        explosionRange++;
        removeItem(item);
        System.out.println("Explosion range increased to: " + explosionRange);
    }

    public int getBombLimit() {
        return bombLimit;
    }


    public double getSpeed() {
        return speed;
    }

    public int getExplosionRange() {
        return explosionRange;
    }

}
