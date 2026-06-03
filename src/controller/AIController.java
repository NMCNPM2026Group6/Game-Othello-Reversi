package controller;

import model.ReversiModel;
import model.ReversiAI;
import model.AiDifficulty;
import view.ReversiView;
import javax.swing.Timer;

/**
 * UC-05: AI đánh quân - AI tự động tính toán và đặt quân tốt nhất (Minimax)
 * 
 * @author ThuyQuynh
 */
public class AIController {
    private ReversiModel model;
    private ReversiView view;
    private ReversiAI ai;
    private boolean aiEnabled = false;
    private int aiPlayer = ReversiModel.WHITE;
    private Timer pendingAiTimer;
    private GamePlayController gamePlayController;

    public AIController(ReversiModel model, ReversiView view, GamePlayController gamePlayController) {
        this.model = model;
        this.view = view;
        this.gamePlayController = gamePlayController;
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

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void cancelPendingAI() {
        if (pendingAiTimer != null && pendingAiTimer.isRunning()) {
            pendingAiTimer.stop();
        }
    }

    /**
     * UC-05 5.1.1: Chuyển lượt và kích hoạt AI
     */
    public void xuLyLuotTiepTheo() {
        int luotTiepTheo = model.getLuotChoiHienTai();

        if (!model.CoNuocDiHopLe(luotTiepTheo)) {
            view.showMessage("Không còn nước đi hợp lệ! Đổi lượt.");
            model.DoiLuot();
            gamePlayController.updateViewFromModel();
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

                // UC-05 5.1.7: model.DatQuanCo(row, col) - đặt quân tại vị trí tối ưu
                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    // UC-05 5.1.8: Cập nhật View sau nước đi của AI
                    gamePlayController.updateViewFromModel();
                    // UC-05 5.1.9: Gọi đệ quy xuLyLuotTiepTheo()
                    xuLyLuotTiepTheo();
                }
            }
        });
        pendingAiTimer.setRepeats(false);
        pendingAiTimer.start();
    }
}
