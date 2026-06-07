package controller;

import view.ReversiView;

/**
 * UC-11: Thoát game - Đóng ứng dụng hoàn toàn
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

    // UC-10/UC-11: Đăng ký các sự kiện điều hướng
    private void registerListeners() {
        // UC-10 10.1.2: view.addBackToMenuListener(e -> returnToMenu())
        view.addBackToMenuListener(e -> returnToMenu());

        // UC-11 11.1.1: addExitListener(e -> exitGame())
        view.getMenuPanel().addExitListener(e -> exitGame());
    }

    public void returnToMenu() {
        aiController.cancelPendingAI();
        view.getMenuPanel().resetToMain();
        view.showMenu();
    }

    /**
     * UC-11 11.1.2: Thoát game hoàn toàn
     */
    public void exitGame() {
        // UC-11 11.1.3: System.exit(0) - đóng ứng dụng
        System.exit(0);
    }
}
