package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.items.Bombitem;
import uet.oop.bomberman.graphics.*;
import uet.oop.bomberman.input.KeyboardHandler;

import java.awt.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class BombermanGame extends Application {
    public enum gameState {
        MENU,
        PLAYING,
        SELECTING_LEVEL,
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
    private int cameraWidth = WIDTH * Sprite.SCALED_SIZE;
    private int cameraHeight =  HEIGHT * Sprite.SCALED_SIZE;

    private GraphicsContext gc;
    private Canvas canvas;
    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> bombs = new ArrayList<>();
    private final List<Entity> stillObjects = new ArrayList<>();
    private final List<Entity> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private Bomber bomber;
    private mainMenu menu;
    private String currentLevel = "";
    private SoundManager soundManager = SoundManager.getInstance();

    private long lastUpdate = 0; // Biến để theo dõi thời gian giữa các frame

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    public void playMusic(){
        if(getCurrentState() == gameState.MENU){
            soundManager.setFilePath("res/sound/01 Title Screen.wav");
            soundManager.play();
        }
        else if(getCurrentState() == gameState.PLAYING && getCurrentLevel() == 1 ){
            soundManager.setFilePath("res/sound/03 Overworld 1 Field Zone Theme.wav");
            soundManager.play();
        }
        else if(getCurrentState() == gameState.OVER){
            soundManager.setFilePath("res/sound/08 Game Over.mp3");
            soundManager.play();
        }
    }

    public static gameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(gameState state) {
        if (state == gameState.MENU || state == gameState.SELECTING_LEVEL) {
            previousState = currentState;
        }
        currentState = state;
        this.playMusic();
    }

    public void restartGame() {
        currentState = gameState.PLAYING;
        previousState = null;

        //clear old data
        bombs.clear();
        entities.clear();
        items.clear();
        enemies.clear();
        stillObjects.clear();

        //recreate entities
        bomber.setHp(3);
        bomber.setDead(false);
        bomber.setExplosionRange(1);
        bomber.setImage(Sprite.player_right.getFxImage());
        createMap(currentLevel);

    }

    public boolean canResume() {
        return previousState == gameState.PLAYING;
    }

    public void resumeGame() {
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

        menu = new mainMenu(this);

        bomber = new Bomber(Sprite.SCALED_SIZE, Sprite.SCALED_SIZE, Sprite.player_right.getFxImage(), stillObjects, bombs, enemies, items);
        new KeyboardHandler(scene, bomber, menu,this);

        stage.setScene(scene);
        stage.show();

        currentLevel = "res/levels/level1.txt";
        createMap(currentLevel);

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
        this.playMusic();
    }

    public void createMap(String levelFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(levelFile))) {
            String firstLine = br.readLine();
            if (firstLine == null) return;

            String[] parts = firstLine.split(" ");
            mapHeight = Integer.parseInt(parts[1]);
            mapWidth = Integer.parseInt(parts[2]);

            for (int j = 0; j < mapHeight; j++) {
                String line = br.readLine();
                if (line == null) break;

                // Đảm bảo chiều dài dòng phù hợp với mapWidth
                if (line.length() < mapWidth) {
                    System.err.println("Lỗi: Dòng trong file không đủ dài. Dòng " + (j + 1) + " có chiều dài " + line.length() + ", trong khi bản đồ yêu cầu chiều dài là " + mapWidth);
                    continue;  // Bỏ qua dòng này và chuyển sang dòng tiếp theo
                }

                for (int i = 0; i < mapWidth; i++) {
                    Entity object = null;
                    Item item = null;
                    char tile = line.charAt(i);

                    switch (tile) {
                        case '#': object = new Wall(i, j, Sprite.wall.getFxImage()); break;
                        case '*': object = new Brick(i, j, Sprite.brick.getFxImage()); break;
                        case 'x': object = new Portal(i, j, Sprite.portal.getFxImage(),enemies,bomber,this); break;
                        case 'p':
                            bomber.setPosition(i * Sprite.SCALED_SIZE, j * Sprite.SCALED_SIZE);
                            entities.add(bomber);
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
                        case '3':
                            object = new BomberEnemy(i, j, Sprite.oneal_left1.getFxImage(), mapWidth, mapHeight, bomber, stillObjects, bombs);
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
                    if (object != null) stillObjects.add(object);
                    if (item != null) items.add(item); // Chỉ thêm item vào danh sách items
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setLevel(int level) {
        // Tải cấp độ mới dựa trên level
        currentState = gameState.PLAYING;
        // Đảm bảo rằng file của các cấp độ có định dạng phù hợp
        switch (level) {
            case 1:
                currentLevel =  "res/levels/level1.txt";
                break;
            case 2:
                currentLevel =  "res/levels/level2.txt";
                break;
            case 3:
                currentLevel =  "res/levels/level3.txt";
                break;
            default:
                currentLevel =  "res/levels/level1.txt";
                break; // Mặc định là level 1 nếu có lỗi
        }
        createMap(currentLevel);
    }

    public void update() {
        if (currentState == gameState.PLAYING) {
            bomber.update();
            checkBomberStatus();
            enemies.forEach(Entity::update);
            enemies.removeIf(enemy -> (enemy instanceof Ghost && !((Ghost) enemy).isAlive()));
            items.removeIf(entity -> entity instanceof Item && entity.isPickedUp());
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

        // Giữ camera trong giới hạn bản đồ
        cameraX = Math.max(0, Math.min(bomberX - cameraWidth / 2, mapWidth * Sprite.SCALED_SIZE - cameraWidth));
        cameraY = Math.max(0, Math.min(bomberY - cameraHeight / 2, mapHeight * Sprite.SCALED_SIZE - cameraHeight));
    }

    public int getCurrentLevel() {
        if (currentLevel.contains("level1")) return 1;
        if (currentLevel.contains("level2")) return 2;
        if (currentLevel.contains("level3")) return 3;
        return 1;
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();
        gc.translate(-cameraX, -cameraY);  // Di chuyển camera trước khi vẽ

        if (currentState == gameState.MENU) {
            gc.restore();
            menu.render(gc);
        }
        else if(currentState == gameState.SELECTING_LEVEL){
            menu.getLevelMenu().render(gc);
        }
        else if (currentState == gameState.PLAYING) {
            updateCamera();
            for (Entity obj : stillObjects) {
                if (isInCamera(obj)) {
                    obj.render(gc);
                }
            }

            for (Entity enemy : enemies) {
                if (isInCamera(enemy)) {
                    enemy.render(gc);
                }
            }

            if (isInCamera(bomber) && bomber.isVisible()) {
                bomber.render(gc);
            }

            for (Entity bomb : bombs) {
                if (isInCamera(bomb)) {
                    bomb.render(gc);
                    for (ExplosionEffect explosion : ((Bomb)bomb).getExplosionEffects()) {
                        if (isInCamera(explosion)) {
                            explosion.render(gc);
                        }
                    }
                }
            }

            for (Item item : items) {
                if (isInCamera(item)) {
                    item.render(gc);
                }
            }
            // Vẽ nền xám
            gc.setFill(Color.web("#404040"));  // Màu xám đậm  // Thiết lập màu nền xám
            gc.fillRect(0, (HEIGHT-2) * Sprite.SCALED_SIZE, WIDTH * Sprite.SCALED_SIZE, 2 * Sprite.SCALED_SIZE);  // Vẽ hình chữ nhật xám

            // Vẽ chữ màu đen
            Font font = Font.font("Arial", FontWeight.BOLD, 30);  // Phông chữ Arial đậm, kích thước 16
            gc.setFont(font);
            gc.setFill(Color.BLACK);  // Thiết lập màu chữ đen
            gc.fillText("LEFT: " + bomber.getHp(), 10, (HEIGHT-1) * Sprite.SCALED_SIZE);  // Vẽ dòng HP

            gc.fillText("Level: " + getCurrentLevel(), (WIDTH - 5)*Sprite.SCALED_SIZE, (HEIGHT-1) * Sprite.SCALED_SIZE);  // Vẽ dòng Level
        } else if (currentState == gameState.OVER) {
            gc.restore();
            menu.getGameoverMenu().render(gc);
        }
        gc.restore();
    }

    private boolean isInCamera(Entity entity) {
        return entity.getX() >= cameraX && entity.getX() < cameraX + WIDTH * Sprite.SCALED_SIZE
                && entity.getY() >= cameraY && entity.getY() < cameraY + HEIGHT * Sprite.SCALED_SIZE;
    }
}
