package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;
import javafx.scene.paint.Color;
import uet.oop.bomberman.SoundManager;

public class mainMenu extends Menu {
    private SelectLevelMenu levelMenu;
    private gameOverMenu gameoverMenu;
    private EscapeMenu escapeMenu;

    public SelectLevelMenu getLevelMenu() {
        return levelMenu;
    }

    public void setLevelMenu(SelectLevelMenu levelMenu) {
        this.levelMenu = levelMenu;
    }

    public gameOverMenu getGameoverMenu() {
        return gameoverMenu;
    }

    public void setGameoverMenu(gameOverMenu gameoverMenu) {
        this.gameoverMenu = gameoverMenu;
    }

    public EscapeMenu getEscapeMenu() {
        return escapeMenu;
    }

    public void setEscapeMenu(EscapeMenu escapeMenu) {
        this.escapeMenu = escapeMenu;
    }

    public mainMenu(BombermanGame game) {
        super(game, new String[]{"Resume Game", "Start Game", "Select Level", "Exit"});
        levelMenu = new SelectLevelMenu(game);
        gameoverMenu = new gameOverMenu(game);
        escapeMenu = new EscapeMenu(game);
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(30));

        for (int i = 0; i < options.length; i++) {
            if (i == 0 && !game.canResume()) {  // Nếu không thể resume, làm xám lựa chọn
                gc.setFill(Color.GRAY);  // Lựa chọn bị vô hiệu hóa, màu xám
                gc.fillText("> " + options[i] + " <", 300, 200 + i * 50);
            }
            else if (i == selectedOption) {
                gc.setFill(Color.BLUE);  // Màu của lựa chọn hiện tại
                gc.fillText("> " + options[i] + " <", 300, 200 + i * 50);
            } else {
                gc.setFill(Color.BLACK);  // Màu của các lựa chọn khác
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

    @Override
    public void select() {
        switch (selectedOption) {
            case 0:
                if (game.canResume()) {
                    game.resumeGame();
                }
                break;
            case 1:
                game.setCurrentState(BombermanGame.gameState.PLAYING);
                break;
            case 2:
                game.setCurrentState(BombermanGame.gameState.SELECTING_LEVEL);
                break;
            case 3:
                System.exit(0);
                break;
        }
    }
}
