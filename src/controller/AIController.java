package controller;

import model.ReversiAI;
import model.AiDifficulty;
import model.ReversiModel;
import view.ReversiView;
import javax.swing.Timer;
import javax.swing.JOptionPane;

/**
 * UC-08: Kết thúc game - Hiển thị kết quả khi cả hai bên hết nước đi
 * 
 * @author ThuyQuynh
 */
public class AIController extends BaseController {
    private ReversiModel model;
    private ReversiView view;
    private ReversiAI ai;
    private boolean aiEnabled = false;
    private int aiPlayer = ReversiModel.WHITE;
    private Timer pendingAiTimer;
    private GamePlayController gamePlayController;
    private Runnable onPlayAgain;
    private Runnable onBackToMenu;

    public AIController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;
    }

    public void setOnPlayAgain(Runnable callback) {
        this.onPlayAgain = callback;
    }

    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void configureAI(boolean enabled, AiDifficulty difficulty) {
        this.aiEnabled = enabled;
        if (enabled) {
            this.aiPlayer = ReversiModel.WHITE;
            this.ai = new ReversiAI(aiPlayer);
            if (difficulty != null) {
                this.ai.setDifficulty(difficulty);
            }
        }
    }

    public void cancelPendingAI() {
        if (pendingAiTimer != null && pendingAiTimer.isRunning()) {
            pendingAiTimer.stop();
        }
    }

    // UC-08 8.1.1: Kiểm tra điều kiện kết thúc game sau mỗi lượt
    public void xuLyLuotTiepTheo() {
        int luotTiepTheo = model.getLuotChoiHienTai();

        // UC-08 8.1.2: model.CoNuocDiHopLe(luotTiepTheo)
        if (!model.CoNuocDiHopLe(luotTiepTheo)) {
            String name = (luotTiepTheo == ReversiModel.BLACK) ? "ĐEN" : "TRẮNG";
            view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");

            model.DoiLuot();
            gamePlayController.updateViewFromModel();

            int luotBanDau = model.getLuotChoiHienTai();
            // UC-08 8.1.3: model.CoNuocDiHopLe(luotBanDau) - kiểm tra phe còn lại
            if (!model.CoNuocDiHopLe(luotBanDau)) {
                // UC-08 8.1.4: Cả hai bên đều không thể đi tiếp -> gameOver()
                gameOver();
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
                int row = bestMove[0];
                int col = bestMove[1];

                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    gamePlayController.updateViewFromModel();
                    // UC-05 5.1.8: Cập nhật View sau nước đi của AI
                    updateViewFromModel();
                    // UC-05 5.1.9: Gọi đệ quy xuLyLuotTiepTheo()
                    xuLyLuotTiepTheo();
                }
            }
        });
        pendingAiTimer.setRepeats(false);
        pendingAiTimer.start();
    }

    /**
     * UC-08 8.1.5: Hiển thị hộp thoại kết thúc và kết quả
     */
    private void gameOver() {
        // UC-08 8.1.6: model.getGameResult() - lấy kết quả thắng/thua/hòa
        String result = model.getGameResult();
        // UC-08 8.1.7: view.showMessage() - hiển thị kết quả chung cuộc
        view.showMessage("TRÒ CHƠI KẾT THÚC!\n" + result);

        // UC-08 8.1.8: JOptionPane.showOptionDialog() - hiển thị 3 lựa chọn
        Object[] options = { "Chơi lại", "Về Menu", "Thoát" };
        int choice = JOptionPane.showOptionDialog(
                view, "Bạn muốn làm gì?", "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // UC-08 8.1.9: Xử lý lựa chọn của người chơi
        if (choice == 0 && onPlayAgain != null) {
            // UC-08 8.1.9a: onPlayAgain.run() - model.resetGame() + updateView
            onPlayAgain.run();
        } else if (choice == 1 && onBackToMenu != null) {
            // UC-08 8.1.9b: onBackToMenu.run() - returnToMenu()
            onBackToMenu.run();
        } else {
            // UC-08 8.1.9c: System.exit(0)
            System.exit(0);
        }
    }
}
