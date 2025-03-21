package uet.oop.bomberman.entities;

import javafx.application.Platform;
import uet.oop.bomberman.graphics.Sprite;

import javafx.scene.image.Image;
import java.util.*;

public class Oneal extends Ghost {
    private final Bomber bomber;
    private int targetX, targetY;
    private final int mapWidth;
    private final int mapHeight;
    private int currentTileX, currentTileY;
    private long lastMoveTime;

    private static final long MIN_DELAY = 200;  // Giới hạn nhỏ nhất (ms)
    private static final long MAX_DELAY = 500;  // Giới hạn lớn nhất (ms)
    private final long MOVE_DELAY = randomDelay();
    private boolean isAlive;
    private int animationStep = 0; // Biến để theo dõi bước của hoạt ảnh
    private boolean movingLeft = true; // Xác định hướng di chuyển

    public Oneal(int x, int y, Image img, int mapWidth, int mapHeight, Bomber bomber) {
        super(x, y, img, bomber.getStillObjects(),bomber.getBombs(),bomber.getEnemies());
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.bomber = bomber;
        this.lastMoveTime = System.currentTimeMillis();
        this.isAlive = true;
    }

    private long randomDelay(){
        return MIN_DELAY + new Random().nextInt((int) (MAX_DELAY - MIN_DELAY + 1));
    }

    public void setTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void move() {
        if (!isAlive || bomber == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < MOVE_DELAY) {
            return;
        }

        currentTileX = this.x / Sprite.SCALED_SIZE;
        currentTileY = this.y / Sprite.SCALED_SIZE;

        setTarget(bomber.getX() / Sprite.SCALED_SIZE, bomber.getY() / Sprite.SCALED_SIZE);
        List<int[]> path = findPathToTarget();

        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0);
            int nextTileX = nextStep[0];
            int nextTileY = nextStep[1];

            if (!isValid(nextTileX, nextTileY)) {
                System.out.println("🚫 Oneal gặp vật cản và không thể di chuyển tiếp.");
                return;
            }

            if (nextTileX < currentTileX) movingLeft = true;  // Nếu x giảm, đang đi sang trái
            else if (nextTileX > currentTileX) movingLeft = false; // Nếu x tăng, đang đi sang phải

            this.x = nextTileX * Sprite.SCALED_SIZE;
            this.y = nextTileY * Sprite.SCALED_SIZE;
            lastMoveTime = currentTime;
        }
    }

    // Tách riêng phương thức update để kiểm tra bom
    public void update() {
        if (!isAlive) {
            this.img = Sprite.oneal_dead.getFxImage();
        }
        move(); // Di chuyển
        animate();//vẽ hoạt ảnh
        checkBombCollision(); // Kiểm tra bom
    }

    private void animate() {
        animationStep = (animationStep + 1) % 3; // Có 3 khung hình cho mỗi hướng

        if (movingLeft) {
            if (animationStep == 0) this.img = Sprite.oneal_left1.getFxImage();
            else if (animationStep == 1) this.img = Sprite.oneal_left2.getFxImage();
            else this.img = Sprite.oneal_left3.getFxImage();
        } else {
            if (animationStep == 0) this.img = Sprite.oneal_right1.getFxImage();
            else if (animationStep == 1) this.img = Sprite.oneal_right2.getFxImage();
            else this.img = Sprite.oneal_right3.getFxImage();
        }
    }

    private void checkBombCollision() {
        if (!isAlive) return;

        int onealTileX = this.x / Sprite.SCALED_SIZE;
        int onealTileY = this.y / Sprite.SCALED_SIZE;

        for (Entity entity : bombs) {
            Bomb bomb = (Bomb) entity;
            int bombTileX = bomb.getX() / Sprite.SCALED_SIZE;
            int bombTileY = bomb.getY() / Sprite.SCALED_SIZE;
            int range = bomb.getBlastRange();

            if (bomb.isExploded()) {
                System.out.println("Checking explosion: Oneal at " + onealTileX + "," + onealTileY +
                        " | Bomb at " + bombTileX + "," + bombTileY + " | Range: " + range);
                if (isInBlastRange(onealTileX, onealTileY, bombTileX, bombTileY, range)) {
                    die();
                    break;
                }
            }
        }
    }

    private boolean isInBlastRange(int x, int y, int bombX, int bombY, int range) {
        if (x == bombX && Math.abs(y - bombY) <= range) return true;
        return y == bombY && Math.abs(x - bombX) <= range;
    }

    public void die() {
        this.isAlive = false;
        this.img = Sprite.oneal_dead.getFxImage(); // Hiển thị ảnh chết
        System.out.println("💀 Oneal đã chết do dính bom!");

        // Xóa khỏi danh sách sau 0.2 giây
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> enemies.remove(Oneal.this));
            }
        }, 2500);
    }

    private List<int[]> findPathToTarget() {
        int startX = this.x / Sprite.SCALED_SIZE;
        int startY = this.y / Sprite.SCALED_SIZE;
        int endX = targetX;
        int endY = targetY;

        // Nếu ô mục tiêu không hợp lệ, tìm ô hợp lệ gần nhất
        if (!isValid(endX, endY)) {
            int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
            for (int[] d : directions) {
                int nx = endX + d[0];
                int ny = endY + d[1];
                if (isValid(nx, ny)) {
                    endX = nx;
                    endY = ny;
                    break;
                }
            }
        }

        Queue<int[]> queue = new LinkedList<>();
        Map<String, int[]> parentMap = new HashMap<>();
        queue.add(new int[]{startX, startY});
        parentMap.put(startX + "," + startY, null);

        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0], cy = current[1];

            if (cx == endX && cy == endY) {
                return reconstructPath(parentMap, cx, cy);
            }

            for (int[] d : directions) {
                int nx = cx + d[0], ny = cy + d[1];

                // Không di chuyển vào ô có vật cản hoặc đã được xét
                if (isValid(nx, ny) && !parentMap.containsKey(nx + "," + ny)) {
                    queue.add(new int[]{nx, ny});
                    parentMap.put(nx + "," + ny, new int[]{cx, cy});
                }
            }
        }

        return null; // Không tìm thấy đường đi hợp lệ
    }

    private List<int[]> reconstructPath(Map<String, int[]> parentMap, int x, int y) {
        List<int[]> path = new ArrayList<>();
        while (parentMap.get(x + "," + y) != null) {
            path.add(new int[]{x, y});
            int[] prev = parentMap.get(x + "," + y);
            x = prev[0];
            y = prev[1];
        }
        Collections.reverse(path);
        return path;
    }

    private boolean isValid(int x, int y) {
        if (x < 0 || y < 0 || x >= mapWidth || y >= mapHeight) {
            return false;
        }
        if (stillObjects == null) {
            System.out.println("⚠️ stillObjects bị null!");
            return false;
        }
        for (Entity entity : stillObjects) {
            int entityX = entity.getX() / Sprite.SCALED_SIZE;
            int entityY = entity.getY() / Sprite.SCALED_SIZE;

            if (entityX == x && entityY == y && !(entity instanceof Grass)) {
                return false; // Không thể đi qua vật cản
            }
        }
        return true;
    }

}