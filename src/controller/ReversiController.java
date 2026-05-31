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

    // Xử lý sự kiện Click vào ô cờ (Nhận tọa độ từ View)
    @Override
    public void actionPerformed(ActionEvent e) {
        // UC-03 3.1.2 / UC-03  3.2.1
        // Tiếp nhận sự kiện nhấn từ View, phân tích tọa độ ô cờ được chọn
        String command = e.getActionCommand();
        String[] coords = command.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);

        // UC-03 3.1.3 / UC-03  3.2.3
        // Gọi phương thức đặt quân cờ trong Model để xử lý nước đi
        boolean DatCoThanhCong = model.DatQuanCo(row, col);

        // UC-03 3.1.9
        // Nếu hợp lệ và đặt thành công, cập nhật giao diện
        if (DatCoThanhCong) {
            updateViewFromModel();
            XuLyLuotTiepTheo();
        }
        // UC-03  3.2.4
        // Nếu DatCoThanhCong trả về false (ô chọn không hợp lệ), giữ nguyên trạng thái chờ click tiếp
    }

    // Điều phối lượt chơi tiếp theo sau khi một người đã đặt cờ
    private void XuLyLuotTiepTheo() {
        int LuotTiepTheo = model.getLuotChoiHienTai();

        // UC-03  3.3.1 / UC-03  3.4.1
        // Kiểm tra xem người chơi kế tiếp có còn nước đi hợp lệ không
        if (!model.CoNuocDiHopLe(LuotTiepTheo)) {
            // UC-03  3.3.2
            // Người chơi kế tiếp bị bỏ lượt, hiển thị thông báo chuyển lượt chơi
            String name = (LuotTiepTheo == ReversiModel.BLACK) ? "ĐEN" : "TRẮNG";
            view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");

            // UC-03  3.3.3
            // Gọi Model thực hiện đổi lại lượt chơi về phe hiện tại
            model.DoiLuot();

            // UC-03  3.3.4
            // Cập nhật lại giao diện hiển thị gợi ý nước đi cho phe hiện tại
            updateViewFromModel();

            // UC-03  3.4.2
            // Tiếp tục kiểm tra xem người chơi hiện tại có còn nước đi hợp lệ không
            int LuotBanDau = model.getLuotChoiHienTai();
            if (!model.CoNuocDiHopLe(LuotBanDau)) {
                // UC-03  3.4.3
                // Cả hai phe đều không còn nước đi hợp lệ, kích hoạt kết thúc trò chơi
                GameOver();
                return;
            }
        }

        // UC-03 3.1.10
        // Trường hợp bình thường, trò chơi tiếp tục diễn ra

        // UC-03  3.5.1
        // Kiểm tra chế độ chơi với AI được bật và lượt hiện tại là của AI
        if (aiEnabled && model.getLuotChoiHienTai() == aiPlayer) {
            // UC-03  3.5.2
            // Kích hoạt xử lý nước đi của AI
            aiMove();
        }
    }

    // Quản lý lượt đánh của AI
    private void aiMove() {
        // UC-03  3.5.3
        // Khởi tạo Timer delay 500ms tạo thời gian chờ xử lý cho AI
        pendingAiTimer = new Timer(500, e -> {
            // UC-03  3.5.4
            // Gọi thuật toán tìm nước đi tối ưu cho AI dựa trên bàn cờ hiện tại
            int[] bestMove = ai.findBestMove(model.getBoard());

            if (bestMove != null) {
                int row = bestMove[0];
                int col = bestMove[1];

                // UC-03  3.5.6
                // Gọi Model thực hiện nước đi của AI tại tọa độ tối ưu
                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    // UC-03  3.5.7
                    // AI đi thành công, cập nhật trạng thái bàn cờ lên View
                    updateViewFromModel();

                    // UC-03  3.5.8
                    // Tiếp tục gọi đệ quy điều phối lượt tiếp theo sau nước đi của AI
                    XuLyLuotTiepTheo();
                }
            }
        });
        pendingAiTimer.setRepeats(false);
        pendingAiTimer.start();
    }

    // Xử lý khi trò chơi kết thúc
    private void GameOver() {
        String result = model.getGameResult();

        // UC-03  3.4.4
        // Hiển thị kết quả chung cuộc thắng/thua/hòa ra màn hình
        view.showMessage("TRÒ CHƠI KẾT THÚC!\n" + result);

        // UC-03  3.4.5
        // Hiển thị hộp thoại 3 lựa chọn (Chơi lại, Về Menu, Thoát) và thực hiện tương ứng
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

    // Kết nối dữ liệu từ Model đẩy ra giao diện
    private void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
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
