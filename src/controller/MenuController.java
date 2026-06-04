package controller;

import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import java.util.function.Consumer;

/**
 * UC-02: Chọn độ khó AI - Chọn mức độ AI: Dễ / Bình thường / Khó (chỉ khi PVE)
 * 
 * @author Đạt Nguyễn
 */
public class MenuController {
    private ReversiView view;
    private Consumer<GameConfig> onGameConfigSelected;

    public MenuController(ReversiView view) {
        this.view = view;
        registerListeners();
    }

    public void setOnGameConfigSelected(Consumer<GameConfig> callback) {
        this.onGameConfigSelected = callback;
    }

    // UC-01/UC-02: Đăng ký sự kiện chọn chế độ chơi và độ khó
    private void registerListeners() {
        // UC-01 1.1.3: addPvpListener -> GameConfig.pvp()
        view.getMenuPanel().addPvpListener(e -> {
            if (onGameConfigSelected != null) {
                onGameConfigSelected.accept(GameConfig.pvp());
            }
        });

        // UC-02 2.1.1: addAiEasyListener -> GameConfig.pve(EASY)
        view.getMenuPanel().addAiEasyListener(e -> {
            if (onGameConfigSelected != null) {
                // UC-02 2.1.2: onGameConfigSelected.accept(GameConfig.pve(AiDifficulty.EASY))
                onGameConfigSelected.accept(GameConfig.pve(AiDifficulty.EASY));
            }
        });

        // UC-02 2.1.3: addAiNormalListener -> GameConfig.pve(NORMAL)
        view.getMenuPanel().addAiNormalListener(e -> {
            if (onGameConfigSelected != null) {
                onGameConfigSelected.accept(GameConfig.pve(AiDifficulty.NORMAL));
            }
        });

        // UC-02 2.1.4: addAiHardListener -> GameConfig.pve(HARD)
        view.getMenuPanel().addAiHardListener(e -> {
            if (onGameConfigSelected != null) {
                onGameConfigSelected.accept(GameConfig.pve(AiDifficulty.HARD));
            }
        });
    }
}
