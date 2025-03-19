package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.items.Bombitem;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.Menu;
import uet.oop.bomberman.graphics.gameOverMenu;
import uet.oop.bomberman.input.KeyboardHandler;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BombermanGame extends Application {
    public enum gameState {
        MENU,
        PLAYING,
        OVER
    }

    private static gameState currentState = gameState.MENU; // Bắt đầu ở menu
    private static gameState previousState = null;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 15;
    private static final double TARGET_FPS = 60; // Giới hạn FPS
    private static final double TIME_PER_FRAME = 1e9 / TARGET_FPS;

    private int mapWidth;
    private int mapHeight;

    private int cameraX = 0;
    private int cameraY = 0;

    private GraphicsContext gc;
    private Canvas canvas;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> bombs = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();
    private List<Entity> enemies = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private Bomber bomber;
    private Menu menu;
    private gameOverMenu gameovermenu;

    private long lastUpdate = 0; // Biến để theo dõi thời gian giữa các frame

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    public static gameState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(gameState state) {
        if (state == gameState.MENU) {
            previousState = currentState;
        }
        currentState = state;
    }

    public static void restartGame() {
        currentState = gameState.PLAYING;
    }

    public static boolean canResume() {
        return previousState == gameState.PLAYING;
    }

    public static void resumeGame() {
        if (previousState == gameState.PLAYING) {
            currentState = gameState.PLAYING;
        }
    }

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        Group root = new Group();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        menu = new Menu();
        gameovermenu = new gameOverMenu();

        bomber = new Bomber(1 * Sprite.SCALED_SIZE, 1 * Sprite.SCALED_SIZE, Sprite.player_right.getFxImage(), stillObjects, bombs, enemies, items);
        new KeyboardHandler(scene, bomber, menu);

        stage.setScene(scene);
        stage.show();

        createMap();
        entities.add(bomber);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= TIME_PER_FRAME) { // Đảm bảo FPS không vượt quá 60
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    public void createMap() {
        try (BufferedReader br = new BufferedReader(new FileReader("res/levels/level1.txt"))) {
            String firstLine = br.readLine();
            if (firstLine == null) return;

            String[] parts = firstLine.split(" ");
            int level = Integer.parseInt(parts[0]);
            mapHeight = Integer.parseInt(parts[1]);
            mapWidth = Integer.parseInt(parts[2]);

            for (int j = 0; j < mapHeight; j++) {
                String line = br.readLine();
                if (line == null) break;

                for (int i = 0; i < mapWidth; i++) {
                    Entity object = null;
                    Item item = null;
                    char tile = line.charAt(i);

                    switch (tile) {
                        case '#': object = new Wall(i, j, Sprite.wall.getFxImage()); break;
                        case '*': object = new Brick(i, j, Sprite.brick.getFxImage()); break;
                        case 'x': object = new Portal(i, j, Sprite.portal.getFxImage()); break;
                        case 'p':
                            bomber.setPosition(i * Sprite.SCALED_SIZE, j * Sprite.SCALED_SIZE);
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                            break;
                        case '1':
                            object = new Balloon(i, j, Sprite.balloom_left1.getFxImage(), bomber);
                            enemies.add(object);
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                            break;
                        case '2':
                            object = new Oneal(i, j, Sprite.oneal_left1.getFxImage(), mapWidth, mapHeight, bomber);
                            enemies.add(object);
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                            break;
                        case 'b':
                        case 'f':
                        case 's':
                            object = new Grass(i, j, Sprite.grass.getFxImage()); // ✅ Đảm bảo ô có cỏ trước
                            stillObjects.add(object);

                            if (tile == 'b') {
                                item = new Bombitem(i, j, Sprite.powerup_bombs.getFxImage(), stillObjects);
                            } else if (tile == 'f') {
                                item = new FlameItem(i, j, Sprite.powerup_flames.getFxImage(), stillObjects);
                            } else if (tile == 's') {
                                item = new SpeedItem(i, j, Sprite.powerup_speed.getFxImage(), stillObjects);
                            }
                            break;
                        default: object = new Grass(i, j, Sprite.grass.getFxImage());
                    }
                    if (object != null){ stillObjects.add(object);}
                    if (item != null) items.add(item); // Chỉ thêm item vào danh sách items
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (currentState == gameState.PLAYING) {
            bomber.update();
            checkBomberStatus();
            enemies.forEach(Entity::update);
            enemies.removeIf(enemy -> (enemy instanceof Ghost && !((Ghost) enemy).isAlive()));
            items.removeIf(entity -> entity instanceof Item && ((Item) entity).isPickedUp());
            bombs.forEach(Entity::update);
            updateCamera();
        }
    }

    public void checkBomberStatus() {
        if (bomber.isDead()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    setCurrentState(gameState.OVER);
                }
            }, 2000);
        }
    }

    private void updateCamera() {
        int bomberX = bomber.getX();
        int bomberY = bomber.getY();

        int maxCameraX = mapWidth * Sprite.SCALED_SIZE - WIDTH * Sprite.SCALED_SIZE;
        int maxCameraY = mapHeight * Sprite.SCALED_SIZE - HEIGHT * Sprite.SCALED_SIZE;

        cameraX = Math.max(0, Math.min(bomberX - WIDTH * Sprite.SCALED_SIZE / 2, maxCameraX));
        cameraY = Math.max(0, Math.min(bomberY - HEIGHT * Sprite.SCALED_SIZE / 2, maxCameraY));
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentState == gameState.MENU) {
            menu.render(gc);
        } else if (currentState == gameState.PLAYING) {
            stillObjects.stream().filter(this::isInCamera).forEach(obj -> obj.render(gc));
            enemies.stream().filter(this::isInCamera).forEach(enemy -> enemy.render(gc));
            if (isInCamera(bomber)) bomber.render(gc);
            bombs.stream().filter(this::isInCamera).forEach(bomb -> bomb.render(gc));
            items.stream().filter(this::isInCamera).forEach(item -> item.render(gc));
        } else if (currentState == gameState.OVER) {
            gameovermenu.render(gc);
        }
    }

    private boolean isInCamera(Entity entity) {
        return entity.getX() >= cameraX && entity.getX() < cameraX + WIDTH * Sprite.SCALED_SIZE
                && entity.getY() >= cameraY && entity.getY() < cameraY + HEIGHT * Sprite.SCALED_SIZE;
    }
}
