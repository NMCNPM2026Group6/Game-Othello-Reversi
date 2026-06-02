package controller;

import model.ReversiModel;
import model.ReversiAI;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class ReversiController implements ActionListener {
    private ReversiModel model;
    private ReversiView view;
    private ReversiAI ai;
    private boolean aiEnabled = true;
    private int aiPlayer = ReversiModel.WHITE;
    private Timer pendingAiTimer;

    // UC-10 và UC-11 handler
    private ReturnToMenuHandler returnToMenuHandler;
    private ExitGameHandler exitGameHandler;

    public ReversiController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;
        this.ai = new ReversiAI(aiPlayer);

        // Khởi tạo handler cho UC-10 và UC-11
        this.returnToMenuHandler = new ReturnToMenuHandler(view);
        this.exitGameHandler = new ExitGameHandler(view);

        this.view.addGameListener(this);

        this.view.getMenuPanel().addPvpListener(e -> {
            configure(GameConfig.pvp());
            this.view.showGame();
        });

        this.view.getMenuPanel().addAiEasyListener(e -> {
            configure(GameConfig.pve(AiDifficulty.EASY));
            this.view.showGame();
        });

        this.view.getMenuPanel().addAiNormalListener(e -> {
            configure(GameConfig.pve(AiDifficulty.NORMAL));
            this.view.showGame();
        });

        this.view.getMenuPanel().addAiHardListener(e -> {
            configure(GameConfig.pve(AiDifficulty.HARD));
            this.view.showGame();
        });

        this.view.getMenuPanel().addHowToPlayListener(e -> {
            new view.HowToPlayDialog(this.view).setVisible(true);
        });

        // UC-11B: Thoát từ Menu chính có xác nhận
        this.view.getMenuPanel().addExitListener(e -> exitGameHandler.executeWithConfirm());

        // UC-10: Quay về Menu từ nút trong GamePanel
        this.view.addBackToMenuListener(e -> returnToMenu());

        this.view.showMenu();
    }

    public void configure(GameConfig config) {
        this.aiEnabled = config.isAiEnabled();
        String info = "Chế độ: " + (aiEnabled ? "Đấu với máy" : "2 Người");
        if (config.isAiEnabled()) {
            this.aiPlayer = ReversiModel.WHITE;
            this.ai = new ReversiAI(aiPlayer);
            this.ai.setDifficulty(config.getDifficulty());
            info += " (" + config.getDifficulty().getDisplayName() + ")";
        }
        view.setModeInfo(info);
        model.resetGame();
        updateViewFromModel();
    }

    /** UC-10: Quay về Menu — uỷ thác cho ReturnToMenuHandler */
    public void returnToMenu() {
        returnToMenuHandler.execute(pendingAiTimer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] coords = command.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);

        boolean DatCoThanhCong = model.DatQuanCo(row, col);
        if (DatCoThanhCong) {
            updateViewFromModel();
            XuLyLuotTiepTheo();
        }
    }

    private void XuLyLuotTiepTheo() {
        int LuotTiepTheo = model.getLuotChoiHienTai();

        if (!model.CoNuocDiHopLe(LuotTiepTheo)) {
            String name = (LuotTiepTheo == ReversiModel.BLACK) ? "ĐEN" : "TRẮNG";
            view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");
            model.DoiLuot();
            updateViewFromModel();

            int LuotBanDau = model.getLuotChoiHienTai();
            if (!model.CoNuocDiHopLe(LuotBanDau)) {
                GameOver();
                return;
            }
        }

        if (aiEnabled && model.getLuotChoiHienTai() == aiPlayer) {
            aiMove();
        }
    }

    private void aiMove() {
        pendingAiTimer = new Timer(500, e -> {
            int[] bestMove = ai.findBestMove(model.getBoard());
            if (bestMove != null) {
                boolean success = model.DatQuanCo(bestMove[0], bestMove[1]);
                if (success) {
                    updateViewFromModel();
                    XuLyLuotTiepTheo();
                }
            }
        });
        pendingAiTimer.setRepeats(false);
        pendingAiTimer.start();
    }

    private void GameOver() {
        String result = model.getGameResult();
        view.showMessage("TRÒ CHƠI KẾT THÚC!\n" + result);

        Object[] options = { "Chơi lại", "Về Menu", "Thoát" };
        int choice = JOptionPane.showOptionDialog(
                view, "Bạn muốn làm gì?", "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            model.resetGame();
            updateViewFromModel();
        } else if (choice == 1) {
            returnToMenu();              // UC-10
        } else {
            exitGameHandler.executeDirect(); // UC-11A
        }
    }

    private void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                model.getValidMoves(model.getLuotChoiHienTai()));
    }

    public void setAiEnabled(boolean enabled) { this.aiEnabled = enabled; }
    public void setAiPlayer(int player) {
        this.aiPlayer = player;
        this.ai = new ReversiAI(player);
    }
}
