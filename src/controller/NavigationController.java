package controller;

import view.ReversiView;

/**
 * UC-10: Quay về Menu - Từ màn hình game quay về Main Menu
 * 
 * @author Lương Hà
 */
public class NavigationController {
    private ReversiView view;
    private AIController aiController;

    public NavigationController(ReversiView view, AIController aiController) {
        this.view = view;
        this.aiController = aiController;
        registerListeners();
    }

    // UC-10 10.1.1: Đăng ký sự kiện click nút "Về Menu"
    private void registerListeners() {
        // UC-10 10.1.2: view.addBackToMenuListener(e -> returnToMenu())
        view.addBackToMenuListener(e -> returnToMenu());
    }

    /**
     * UC-10 10.1.3: Quay về Main Menu và dừng các tiến trình chạy ngầm
     */
    public void returnToMenu() {
        // UC-10 10.1.4: aiController.cancelPendingAI() - dừng Timer AI
        aiController.cancelPendingAI();
        // UC-10 10.1.5: view.getMenuPanel().resetToMain() - reset trạng thái menu
        view.getMenuPanel().resetToMain();
        // UC-10 10.1.6: view.showMenu() - chuyển CardLayout về "MENU"
        view.showMenu();
    }
}
