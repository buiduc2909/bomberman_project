package uet.oop.bomberman.entities;

import javafx.application.Platform;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.Sprite;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Bomb extends Entity {
    private final List<Entity> stillObjects;
    private final List<Entity> bombs;
    private boolean exploded = false;
    private int explosionIndex = 0;
    private List<ExplosionEffect> explosionEffects = new ArrayList<>();
    private final int blastRange;

    private final Image[][] explosionSprites = {
            {Sprite.explosion_horizontal_left_last.getFxImage(), Sprite.explosion_horizontal_left_last1.getFxImage(), Sprite.explosion_horizontal_left_last2.getFxImage()},
            {Sprite.explosion_horizontal_right_last.getFxImage(), Sprite.explosion_horizontal_right_last1.getFxImage(), Sprite.explosion_horizontal_right_last2.getFxImage()},
            {Sprite.explosion_vertical_top_last.getFxImage(), Sprite.explosion_vertical_top_last1.getFxImage(), Sprite.explosion_vertical_top_last2.getFxImage()},
            {Sprite.explosion_vertical_down_last.getFxImage(), Sprite.explosion_vertical_down_last1.getFxImage(), Sprite.explosion_vertical_down_last2.getFxImage()}
    };

    public List<ExplosionEffect> getExplosionEffects() {
        System.out.println("📢 Returning explosionEffects size: " + explosionEffects.size());
        return explosionEffects;
    }

    public void setExplosionEffects(List<ExplosionEffect> explosionEffects) {
        this.explosionEffects = explosionEffects;
    }

    public Bomb(int x, int y, List<Entity> stillObjects, List<Entity> bombs, int blastRange) {
        super(x * Sprite.SCALED_SIZE, y * Sprite.SCALED_SIZE, Sprite.bomb.getFxImage());
        this.stillObjects = stillObjects;
        this.bombs = bombs;
        this.x = x * Sprite.SCALED_SIZE;
        this.y = y * Sprite.SCALED_SIZE;
        this.blastRange = blastRange;
        System.out.println("🧨 Bomb placed at " + x + ", " + y);
        triggerExplosion();
    }

    private void triggerExplosion() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    exploded = true;
                    System.out.println("💥 Bomb exploded at " + (x / Sprite.SCALED_SIZE) + ", " + (y / Sprite.SCALED_SIZE));
                    explode();
                });
            }
        }, 2000);
    }

    private void explode() {
        int bombX = this.x / Sprite.SCALED_SIZE;
        int bombY = this.y / Sprite.SCALED_SIZE;

        for (int i = 1; i <= blastRange; i++) {
            boolean isLast = (i == blastRange);

            checkAndReplace(bombX + i, bombY);
            createExplosionEffect(bombX + i, bombY, 1, isLast);
            System.out.println("💥 Right explosion at (" + (bombX + i) + ", " + bombY + ")");

            checkAndReplace(bombX - i, bombY);
            createExplosionEffect(bombX - i, bombY, 0, isLast);
            System.out.println("💥 Left explosion at (" + (bombX - i) + ", " + bombY + ")");

            checkAndReplace(bombX, bombY + i);
            createExplosionEffect(bombX, bombY + i, 3, isLast);
            System.out.println("💥 Down explosion at (" + bombX + ", " + (bombY + i) + ")");

            checkAndReplace(bombX, bombY - i);
            createExplosionEffect(bombX, bombY - i, 2, isLast);
            System.out.println("💥 Up explosion at (" + bombX + ", " + (bombY - i) + ")");
        }

        animateExplosion();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> bombs.remove(Bomb.this));
            }
        }, 600);
        System.out.println("🔥 Explosion animation started");
    }


    private void checkAndReplace(int x, int y) {
        for (int i = 0; i < stillObjects.size(); i++) {
            Entity obj = stillObjects.get(i);
            int objX = obj.getX() / Sprite.SCALED_SIZE;
            int objY = obj.getY() / Sprite.SCALED_SIZE;

            if (objX == x && objY == y && obj instanceof Brick) {
                System.out.println("🧱 Brick destroyed at " + x + ", " + y);
                ((Brick) obj).explode();
                final int index = i;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> stillObjects.set(index, new Grass(x, y, Sprite.grass.getFxImage())));
                    }
                }, 600);
            }
        }

    }

    private void createExplosionEffect(int x, int y, int direction, boolean isLast) {
        System.out.println("💥 Explosion effect created at " + x + ", " + y);
        Image img = isLast ? explosionSprites[direction][0] : Sprite.explosion_horizontal.getFxImage();
        if (direction == 2 || direction == 3) {
            img = isLast ? explosionSprites[direction][0] : Sprite.explosion_vertical.getFxImage();
        }
        ExplosionEffect explosion = new ExplosionEffect(x, y, img, direction);
        explosionEffects.add(explosion);
    }


    private void animateExplosion() {
        Timer explosionTimer = new Timer();
        explosionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (explosionIndex < explosionSprites[0].length) {
                        for (ExplosionEffect explosion : explosionEffects) {
                            explosion.updateSprite(explosionSprites[explosion.getDirection()][explosionIndex]);
                        }
                        System.out.println("🎞 Explosion animation frame " + explosionIndex);
                        explosionIndex++;
                    } else {
                        for (ExplosionEffect explosion : explosionEffects) {
                            explosion.updateSprite(Sprite.grass.getFxImage());
                        }
                        explosionEffects.clear();
                        explosionTimer.cancel();
                        System.out.println("✅ Explosion animation finished");
                    }
                });
            }
        }, 0, 100);
    }

    public boolean isExploded() {
        return exploded;
    }

    public int getBlastRange() {
        return 1;
    }

    @Override
    public void update() {
        if (exploded) {
            setImg(Sprite.bomb_exploded.getFxImage());
        }
    }
}