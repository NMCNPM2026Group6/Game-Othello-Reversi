package controller;

import view.HowToPlayDialog;
import view.ReversiView;

public class HelpController {
    private ReversiView view;

    public HelpController(ReversiView view) {
        this.view = view;
        registerListeners();
    }

    // UC-07 7.1.1: Đăng ký sự kiện click nút "Hướng dẫn chơi" trên Menu
    private void registerListeners() {
        // UC-07 7.1.2: addHowToPlayListener(e -> showHowToPlay())
        view.getMenuPanel().addHowToPlayListener(e -> {
            showHowToPlay();
        });
    }

    // UC-07 7.1.3: Tạo và hiển thị HowToPlayDialog
    public void showHowToPlay() {
        // UC-07 7.1.4: new HowToPlayDialog(view).setVisible(true)
        HowToPlayDialog dialog = new HowToPlayDialog(view);
        dialog.setVisible(true);
    }
}
