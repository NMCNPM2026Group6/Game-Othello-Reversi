package controller;

import model.ReversiModel;
import model.ReversiAI;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ReversiController implements ActionListener {
    private ReversiModel model;
    private ReversiView view;
    private ReversiAI ai;
    private boolean aiEnabled = true; // Bật/tắt AI
    private int aiPlayer = ReversiModel.WHITE; // AI chơi màu TRẮNG
    private Timer pendingAiTimer; // Tránh memory leak và move sai khi quay lại menu

    public ReversiController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;
        this.ai = new ReversiAI(aiPlayer);

        // dang ky su kien
        this.view.addGameListener(this);

        // menu listeners
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

        this.view.getMenuPanel().addExitListener(e -> System.exit(0));

        this.view.addBackToMenuListener(e -> returnToMenu());

        // Hiển thị Menu khi khởi động
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

    public void returnToMenu() {
        if (pendingAiTimer != null && pendingAiTimer.isRunning()) {
            pendingAiTimer.stop();
        }
        view.getMenuPanel().resetToMain();
        view.showMenu();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] coords = command.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);

        // thuc hien nuoc di
        boolean DatCoThanhCong = model.DatQuanCo(row, col);

        if (DatCoThanhCong) {
            // UC-04 4.1.1: Sau khi đặt quân thành công, kích hoạt cập nhật gợi ý
            updateViewFromModel();

            // xu ly sau khi di
            XuLyLuotTiepTheo();
        }
    }

    private void XuLyLuotTiepTheo() {
        int LuotTiepTheo = model.getLuotChoiHienTai();

        // kiem tra nguoi ke tiep co di duoc khong
        if (!model.CoNuocDiHopLe(LuotTiepTheo)) {
            // nguoi ke tiep khong di duoc
            String name = (LuotTiepTheo == ReversiModel.BLACK) ? "ĐEN" : "TRẮNG";
            view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");

            // trả lai luot
            model.DoiLuot();
            updateViewFromModel();

            // kiem tra nguoi vua danh co di duoc khong
            int LuotBanDau = model.getLuotChoiHienTai();
            if (!model.CoNuocDiHopLe(LuotBanDau)) {
                // ca 2 deu khong di duoc
                GameOver();
                return;
            }
        }

        // UC-05 5.1.2: Kiểm tra aiEnabled && lượt hiện tại == aiPlayer
        if (aiEnabled && model.getLuotChoiHienTai() == aiPlayer) {
            // UC-05 5.1.3: Kích hoạt aiMove()
            aiMove();
        }
    }

    // UC-05 5.1.4: Quản lý lượt đánh của AI
    private void aiMove() {
        // UC-05 5.1.5: Khởi tạo Timer delay 500ms
        pendingAiTimer = new Timer(500, e -> {
            // UC-05 5.1.6: ai.findBestMove(model.getBoard()) - Minimax + Alpha-Beta
            int[] bestMove = ai.findBestMove(model.getBoard());

            if (bestMove != null) {
                int row = bestMove[0];
                int col = bestMove[1];

                // UC-05 5.1.7: model.DatQuanCo(row, col)
                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    // UC-05 5.1.8: Cập nhật View
                    updateViewFromModel();
                    // UC-05 5.1.9: Đệ quy XuLyLuotTiepTheo()
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
        int choice = javax.swing.JOptionPane.showOptionDialog(
                view, "Bạn muốn làm gì?", "Game Over",
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { // Chơi lại
            model.resetGame();
            updateViewFromModel();
        } else if (choice == 1) { // Về Menu
            returnToMenu();
        } else { // Thoát
            System.exit(0);
        }
    }

    // UC-04 4.1.2: Lấy danh sách nước đi hợp lệ từ Model và truyền cho View
    private void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                // UC-04 4.1.3: model.getValidMoves() trả về boolean[][] các ô hợp lệ
                model.getValidMoves(model.getLuotChoiHienTai()));
    }

    // Bật/tắt AI
    public void setAiEnabled(boolean enabled) {
        this.aiEnabled = enabled;
    }

    // Đổi màu AI chơi
    public void setAiPlayer(int player) {
        this.aiPlayer = player;
        this.ai = new ReversiAI(player);
    }
}
