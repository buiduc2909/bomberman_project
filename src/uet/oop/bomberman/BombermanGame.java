package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.entities.*;
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

    private static gameState currentState = gameState.MENU; //bat dau o menu
    private static gameState previousState = null;

    public static final int WIDTH = 20;  // Kích thước màn hình hiển thị
    public static final int HEIGHT = 15;

    private int mapWidth;  // Chiều rộng thực tế của bản đồ (tính theo ô)
    private int mapHeight; // Chiều cao thực tế của bản đồ

    private int cameraX = 0;  // Vị trí X của camera
    private int cameraY = 0;  // Vị trí Y của camera

    private GraphicsContext gc;
    private Canvas canvas;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> bombs = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();
    private List<Entity> enemies = new ArrayList<>();
    private Bomber bomber;
    private Menu menu;
    private gameOverMenu gameovermenu;

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    public static gameState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(gameState state) {
        if (state == BombermanGame.gameState.MENU) {
            previousState = currentState;  // Lưu trạng thái trước đó
        }
        BombermanGame.currentState = state;
    }

    public static void restartGame() {
        currentState = gameState.PLAYING;
    }

    public static boolean canResume() {
        return previousState == gameState.PLAYING;  // Chỉ có thể Resume nếu trước đó đang chơi
    }

    public static void resumeGame() {
        if (previousState == gameState.PLAYING) {
            currentState = gameState.PLAYING;
        }
    }

    @Override
    public void start(Stage stage) {
        // Tạo Canvas với kích thước màn hình
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tạo root container
        Group root = new Group();
        root.getChildren().add(canvas);

        // Tạo Scene
        Scene scene = new Scene(root);

        //tao menu
        menu = new Menu();
        gameovermenu = new gameOverMenu();

        // Tạo nhân vật chính (Bomber)
        bomber = new Bomber(1 * Sprite.SCALED_SIZE, 1 * Sprite.SCALED_SIZE, Sprite.player_right.getFxImage(), stillObjects, bombs,enemies);
        new KeyboardHandler(scene, bomber,menu);

        // Thêm scene vào stage
        stage.setScene(scene);
        stage.show();

        // Tạo map
        createMap();

        // Thêm bomber vào danh sách thực thể
        entities.add(bomber);

        // Vòng lặp game
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
                render();
            }
        };
        timer.start();
    }

    public void createMap() {
        try (BufferedReader br = new BufferedReader(new FileReader("res/levels/level1.txt"))) {
            String firstLine = br.readLine(); // Đọc dòng đầu tiên
            if (firstLine == null) return;

            String[] parts = firstLine.split(" ");
            int level = Integer.parseInt(parts[0]);
            mapHeight = Integer.parseInt(parts[1]);  // Cập nhật kích thước bản đồ thực tế
            mapWidth = Integer.parseInt(parts[2]);

            for (int j = 0; j < mapHeight; j++) {
                String line = br.readLine();
                if (line == null) break;

                for (int i = 0; i < mapWidth; i++) {
                    Entity object = null;
                    char tile = line.charAt(i);

                    switch (tile) {
                        case '#':
                            object = new Wall(i, j, Sprite.wall.getFxImage());
                            break;
                        case '*':
                            object = new Brick(i, j, Sprite.brick.getFxImage());
                            break;
                        case 'x':
                            object = new Portal(i, j, Sprite.portal.getFxImage());
                            break;
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
                            object = new Oneal(i, j, Sprite.oneal_left1.getFxImage(), mapWidth, mapHeight,bomber);
                            enemies.add(object);
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                            break;
                        case 'b':
                            object = new Bombitem(i, j, Sprite.powerup_bombs.getFxImage());
                            break;
                        case 'f':
                            object = new FlameItem(i, j, Sprite.powerup_flames.getFxImage());
                            break;
                        case 's':
                            object = new SpeedItem(i, j, Sprite.powerup_speed.getFxImage());
                            break;
                        default:
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                    }
                    stillObjects.add(object);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if(currentState == gameState.PLAYING) {
            bomber.update();
            checkBomberStatus();
            // Dùng Iterator để xóa kẻ địch khi chết
            Iterator<Entity> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Entity enemy = iterator.next();
                enemy.update();
                if (enemy instanceof Ghost && !((Ghost) enemy).isAlive()) {
                    iterator.remove();
                }
            }

            // Cập nhật bom
            bombs.forEach(Entity::update);

            // Cập nhật camera
            updateCamera();
        }
    }

    public void checkBomberStatus() {
        if(bomber.isDead()){
            // Tạo Timer để delay 2 giây trước khi set trạng thái OVER
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setCurrentState(gameState.OVER);
                    timer.cancel(); // Hủy timer sau khi hoàn thành
                }
            }, 2000); // Delay 2 giây (2000 ms)
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

        if(currentState == gameState.MENU) {
            menu.render(gc);
        }
        else if(currentState == gameState.PLAYING) {
            // Vẽ nền bản đồ trong camera
            int startX = cameraX / Sprite.SCALED_SIZE;
            int startY = cameraY / Sprite.SCALED_SIZE;
            int endX = Math.min(startX + WIDTH, mapWidth);
            int endY = Math.min(startY + HEIGHT, mapHeight);

            for (int j = startY; j < endY; j++) {
                for (int i = startX; i < endX; i++) {
                    stillObjects.get(j * mapWidth + i).render(gc);
                }
            }

            // Vẽ kẻ địch
            for (Entity enemy : enemies) {
                if (isInCamera(enemy)) {
                    enemy.render(gc);
                }
            }

            // Vẽ nhân vật chính
            if (isInCamera(bomber)) {
                bomber.render(gc);
            }

            // Vẽ bom
            for (Entity bomb : bombs) {
                if (isInCamera(bomb)) {
                    bomb.render(gc);
                    if (bomb instanceof Bomb) {
                        for (Entity explosion : ((Bomb) bomb).getExplosionEffects()) {
                            if (isInCamera(explosion)) {
                                explosion.render(gc);
                            }
                        }
                    }
                }
            }
        }
        else if(currentState == gameState.OVER){
            gameovermenu.render(gc);
        }
    }

    // Kiểm tra một thực thể có trong phạm vi camera không
    private boolean isInCamera(Entity entity) {
        return entity.getX() >= cameraX && entity.getX() < cameraX + WIDTH * Sprite.SCALED_SIZE
                && entity.getY() >= cameraY && entity.getY() < cameraY + HEIGHT * Sprite.SCALED_SIZE;
    }
}
