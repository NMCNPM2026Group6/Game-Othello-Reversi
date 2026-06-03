package controller;

import model.GameConfig;
import view.ReversiView;
import java.util.function.Consumer;

/**
 * UC-01: Chọn chế độ chơi - Chọn PVP (2 người) hoặc PVE (đấu máy) từ Main Menu
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

    // UC-01 1.1.1: Thiết lập callback xử lý khi người chơi chọn chế độ
    public void setOnGameConfigSelected(Consumer<GameConfig> callback) {
        this.onGameConfigSelected = callback;
    }

    // UC-01 1.1.2: Đăng ký sự kiện click các nút chế độ chơi
    private void registerListeners() {
        // UC-01 1.1.3: addPvpListener -> GameConfig.pvp() - chế độ 2 người chơi
        view.getMenuPanel().addPvpListener(e -> {
            if (onGameConfigSelected != null) {
                // UC-01 1.1.4: onGameConfigSelected.accept(GameConfig.pvp())
                onGameConfigSelected.accept(GameConfig.pvp());
            }
        });
    }
}
