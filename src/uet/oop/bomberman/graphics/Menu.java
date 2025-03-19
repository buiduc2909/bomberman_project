package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;


public class Menu {
    protected int selectedOption = 0;  // 0: Start, 1: Exit
    protected String[] options = {"Resume Game","Start Game", "Exit"};

    public Image background;
    public Menu() {
        try {
            String path = getClass().getResource("/textures/main_menu_background.png").toExternalForm();
            background = new Image(path);
        } catch (Exception e) {
            System.out.println("Lỗi tải ảnh nền menu: " + e.getMessage());
        }
    }


    public void render(GraphicsContext gc) {
        gc.drawImage(background, 0, 0, BombermanGame.WIDTH * Sprite.SCALED_SIZE, BombermanGame.HEIGHT * Sprite.SCALED_SIZE);

        //vẽ menu
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(30));

        for (int i = 0; i < options.length; i++) {
            if(i == 0 && !BombermanGame.canResume()){
                gc.setFill(Color.GRAY);//vo hieu hoa resume game
            }
            else if (i == selectedOption) {
                gc.setFill(Color.BLUE);
                gc.fillText("> " + options[i] + " <", 300, 200 + i * 50);
            } else {
                gc.setFill(Color.BLACK);
                gc.fillText(options[i], 300, 200 + i * 50);
            }
        }
    }

    public void moveUp() {
        selectedOption = (selectedOption - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedOption = (selectedOption + 1) % options.length;
    }

    public void select() {
        if (selectedOption == 0 && BombermanGame.canResume()) {
            BombermanGame.resumeGame();
        }
        else if (selectedOption == 1) {
            BombermanGame.setCurrentState(BombermanGame.gameState.PLAYING);
        }
        else if(selectedOption == 2) {
            System.exit(0);
        }
    }
}

