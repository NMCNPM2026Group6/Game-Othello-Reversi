package controller;

import model.ReversiModel;
import model.ReversiAI;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ReversiController extends BaseController implements ActionListener {
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

        // 7.2 kích hoạt lambda
        this.view.getMenuPanel().addHowToPlayListener(e -> {
            // 7.7 gọi lệnh setVisible = true ở đây khi có yêu cầu
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
            // UC-06 6.1.1: Sau khi đặt quân thành công, cập nhật điểm số lên View
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
                // 9.1 hiển thị hộp thoại kết quả: chơi lại, về menu, thoát
                GameOver();
                return;
            }
        }

        // Nếu đến lượt AI thì AI tự động đi
        if (aiEnabled && model.getLuotChoiHienTai() == aiPlayer) {
            aiMove();
        }
    }

    // AI thực hiện nước đi
    private void aiMove() {
        // Dùng Timer để delay một chút, tránh AI đi ngay lập tức
        pendingAiTimer = new Timer(500, e -> {
            // Tìm nước đi tốt nhất
            int[] bestMove = ai.findBestMove(model.getBoard());

            if (bestMove != null) {
                int row = bestMove[0];
                int col = bestMove[1];

                // Thực hiện nước đi
                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    updateViewFromModel();
                    XuLyLuotTiepTheo();
                }
            }
        });
        pendingAiTimer.setRepeats(false);
        pendingAiTimer.start();
    }

    // 9.1b1 hàm này có thể được gọi lại nhiều lần theo hành động người chơi
    private void GameOver() {
        String result = model.getGameResult();
        view.showMessage("TRÒ CHƠI KẾT THÚC!\n" + result);

        // 9.2 người chơi click nút chơi lại với choice = 0
        Object[] options = { "Chơi lại", "Về Menu", "Thoát" };
        int choice = javax.swing.JOptionPane.showOptionDialog(
                view, "Bạn muốn làm gì?", "Game Over",
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        // 9.3 kiểm tra điều kiện
        if (choice == 0) { // Chơi lại
            // 9.4 gọi lệnh khởi tạo lại lõi
            model.resetGame();
            // 9.9 gọi nội bộ update giao diện nước đi
            updateViewFromModel();
        } else if (choice == 1) { // Về Menu
            returnToMenu();
        } else { // Thoát
            System.exit(0);
        }
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
