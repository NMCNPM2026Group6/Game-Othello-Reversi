package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;
import model.ReversiAI;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import controller.ReversiController;
import java.awt.event.ActionEvent;

/**
 * ===================================================================
 * UC-05: AI ĐÁNH QUÂN — KIỂM THỬ LUỒNG CHÍNH (Main Flow)
 * ===================================================================
 * Tham chiếu: flow/Uc-05/Main Flow.txt
 *
 * Luồng chính mô tả quy trình AI tự động đánh quân:
 *   5.1.0. XuLyLuotTiepTheo() xác nhận aiEnabled && currentPlayer == aiPlayer → true
 *   5.1.1. Controller gọi aiMove()
 *   5.1.2. Tạo Timer(500ms) để delay
 *   5.1.3. Timer callback kích hoạt sau 500ms
 *   5.1.4. Gọi ai.findBestMove(model.getBoard())
 *   5.1.5. Minimax + Alpha-Beta Pruning tìm nước đi tối ưu
 *   5.1.6. AI trả về heuristic kết hợp positionWeight + mobility
 *   5.1.7. Trả về bestMove[row, col]
 *   5.1.8. Controller gọi model.DatQuanCo(row, col)
 *   5.1.9. DatQuanCo() trả về true
 *   5.1.10. Controller gọi updateViewFromModel()
 *   5.1.11. View được cập nhật
 *   5.1.12. XuLyLuotTiepTheo() đệ quy kiểm tra lượt kế tiếp
 *
 * Các test case bao phủ:
 *   - Bước 5.1.0: Kiểm tra AI chỉ kích hoạt khi đúng điều kiện PVE
 *   - Bước 5.1.2–5.1.3: Timer(500ms) delay hoạt động đúng
 *   - Bước 5.1.4–5.1.7: AI tìm nước đi thành công qua findBestMove()
 *   - Bước 5.1.8–5.1.9: DatQuanCo() đặt quân AI thành công
 *   - Bước 5.1.12: Lượt trở về Player sau khi AI đi
 */
public class UC05_MainFlow_AiDanhQuanTest {
    private ReversiModel model;
    private ReversiView view;
    private ReversiController controller;

    @Before
    public void setUp() {
        model = new ReversiModel();
        view = new ReversiView();
        controller = new ReversiController(model, view);
    }

    private int demOchoTrong(int[][] board) {
        int count = 0;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] == ReversiModel.EMPTY) count++;
        return count;
    }

    // =====================================================================
    // Bước 5.1.0–5.1.12: Luồng PVE hoàn chỉnh
    // Player đi nước đầu → AI tự động tìm và đặt quân sau 500ms
    //   → Lượt trở về Player → bàn cờ có thêm 2 quân mới
    // =====================================================================
    @Test
    public void testStep5_1_LuongPVE_HoanChinh() throws Exception {
        controller.configure(GameConfig.pve(AiDifficulty.EASY));
        int emptyBefore = demOchoTrong(model.getBoard());

        // Player (Đen) đặt quân tại (2,3)
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        controller.actionPerformed(event);

        // 5.1.0: Sau nước đi Player, lượt chuyển sang AI (Trắng)
        assertEquals("5.1.0: Lượt chuyển sang AI (Trắng)",
                ReversiModel.WHITE, model.getLuotChoiHienTai());

        // 5.1.2–5.1.3: Chờ Timer(500ms) + buffer
        Thread.sleep(700);

        // 5.1.12: Sau khi AI đi xong, XuLyLuotTiepTheo() trả lượt về Player
        assertEquals("5.1.12: Lượt trở về Player (Đen)",
                ReversiModel.BLACK, model.getLuotChoiHienTai());

        // 5.1.8–5.1.9: Bàn cờ có thêm 2 quân (1 Player + 1 AI)
        int emptyAfter = demOchoTrong(model.getBoard());
        assertEquals("5.1.8: Bàn cờ giảm 2 ô trống (Player + AI đặt quân)",
                emptyBefore - 2, emptyAfter);

        System.out.println("  [PASS] UC-05 Main Flow: testStep5_1_LuongPVE_HoanChinh - Luồng PVE hoàn chỉnh: Player → AI (500ms delay) → Player.");
    }

    // =====================================================================
    // Bước 5.1.4–5.1.7: AI findBestMove() trả về toạ độ hợp lệ
    // Kiểm tra trực tiếp ReversiAI trên bàn cờ khởi tạo chuẩn
    // =====================================================================
    @Test
    public void testStep5_1_4_FindBestMoveTraVeToaDoHopLe() {
        ReversiAI ai = new ReversiAI(ReversiModel.WHITE);
        ai.setDifficulty(AiDifficulty.NORMAL);

        int[] bestMove = ai.findBestMove(model.getBoard());

        // 5.1.7: bestMove phải khác null và nằm trong phạm vi [0-7]
        assertNotNull("5.1.7: bestMove không được null", bestMove);
        assertTrue("5.1.7: Hàng phải trong [0,7]", bestMove[0] >= 0 && bestMove[0] < 8);
        assertTrue("5.1.7: Cột phải trong [0,7]", bestMove[1] >= 0 && bestMove[1] < 8);

        // Nước đi phải hợp lệ cho Trắng (AI)
        // Đổi lượt sang Trắng trước khi kiểm tra
        model.DoiLuot();
        boolean hopLe = model.DatQuanCo(bestMove[0], bestMove[1]);
        assertTrue("5.1.8: DatQuanCo() tại bestMove phải thành công", hopLe);

        System.out.println("  [PASS] UC-05 Main Flow: testStep5_1_4_FindBestMoveTraVeToaDoHopLe - AI tìm được nước đi hợp lệ tại ("
                + bestMove[0] + "," + bestMove[1] + ").");
    }

    // =====================================================================
    // Bước 5.1.5–5.1.6: Minimax + Alpha-Beta Pruning đáp ứng yêu cầu thời gian
    // EASY (depth=2) < 100ms, NORMAL (depth=5) < 800ms, HARD (depth=8) < 5000ms
    // =====================================================================
    @Test
    public void testStep5_1_5_HieuNangMinimaxAlphaBeta() {
        ReversiAI ai = new ReversiAI(ReversiModel.WHITE);
        int[][] board = model.getBoard();

        // EASY (depth=2)
        ai.setDifficulty(AiDifficulty.EASY);
        long start = System.currentTimeMillis();
        ai.findBestMove(board);
        long duration = System.currentTimeMillis() - start;
        assertTrue("5.1.5: AI Dễ (depth=2) phải xong trong 100ms, thực tế: " + duration + "ms",
                duration < 100);

        // NORMAL (depth=5)
        ai.setDifficulty(AiDifficulty.NORMAL);
        start = System.currentTimeMillis();
        ai.findBestMove(board);
        duration = System.currentTimeMillis() - start;
        assertTrue("5.1.5: AI Bình thường (depth=5) phải xong trong 800ms, thực tế: " + duration + "ms",
                duration < 800);

        // HARD (depth=8)
        ai.setDifficulty(AiDifficulty.HARD);
        start = System.currentTimeMillis();
        ai.findBestMove(board);
        duration = System.currentTimeMillis() - start;
        assertTrue("5.1.5: AI Khó (depth=8) phải xong trong 5000ms, thực tế: " + duration + "ms",
                duration < 5000);

        System.out.println("  [PASS] UC-05 Main Flow: testStep5_1_5_HieuNangMinimaxAlphaBeta - Thời gian tính toán đáp ứng yêu cầu tất cả độ khó.");
    }

    // =====================================================================
    // Bước 5.1.6: Heuristic ưu tiên chiếm GÓC (positionWeight = +100)
    // Thiết lập: AI có thể chọn góc (0,0) hoặc ô thường (3,0)
    //   → AI phải chọn góc (0,0) vì trọng số cao hơn (+100 so với ô thường)
    // =====================================================================
    @Test
    public void testStep5_1_6_HeuristicUuTienGoc() {
        ReversiAI ai = new ReversiAI(ReversiModel.WHITE);
        ai.setDifficulty(AiDifficulty.EASY);

        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        // Tạo tình huống: AI (Trắng) có thể đặt tại góc (0,0)
        board[0][2] = ReversiModel.WHITE;
        board[0][1] = ReversiModel.BLACK;
        // Và cũng có thể đặt tại ô thường (3,0)
        board[3][2] = ReversiModel.WHITE;
        board[3][1] = ReversiModel.BLACK;

        int[] bestMove = ai.findBestMove(board);

        assertNotNull("bestMove không được null", bestMove);
        assertEquals("5.1.6: AI phải chọn hàng 0 (góc)", 0, bestMove[0]);
        assertEquals("5.1.6: AI phải chọn cột 0 (góc)", 0, bestMove[1]);

        System.out.println("  [PASS] UC-05 Main Flow: testStep5_1_6_HeuristicUuTienGoc - Heuristic AI ưu tiên chiếm góc (positionWeight = +100).");
    }

    // =====================================================================
    // Kiểm tra AI tính toán KHÔNG LÀM THAY ĐỔI bàn cờ chính (Board Isolation)
    // findBestMove() phải tạo bản sao nội bộ, không mutate board gốc
    // =====================================================================
    @Test
    public void testBoardIsolation_AiKhongThayDoiBanCoGoc() {
        ReversiAI ai = new ReversiAI(ReversiModel.WHITE);
        ai.setDifficulty(AiDifficulty.NORMAL);

        int[][] board = model.getBoard();
        int[][] banSao = new int[8][8];
        for (int r = 0; r < 8; r++)
            System.arraycopy(board[r], 0, banSao[r], 0, 8);

        ai.findBestMove(board);

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                assertEquals("Ô (" + r + "," + c + ") không được thay đổi",
                        banSao[r][c], board[r][c]);

        System.out.println("  [PASS] UC-05 Main Flow: testBoardIsolation_AiKhongThayDoiBanCoGoc - AI tính toán độc lập, không thay đổi bàn cờ gốc.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC05_MainFlow_AiDanhQuanTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-05 Main Flow: testStep5_1_4_FindBestMoveTraVeToaDoHopLe - AI tìm được nước đi hợp lệ.
.  [PASS] UC-05 Main Flow: testStep5_1_6_HeuristicUuTienGoc - Heuristic AI ưu tiên chiếm góc (positionWeight = +100).
.  [PASS] UC-05 Main Flow: testBoardIsolation_AiKhongThayDoiBanCoGoc - AI tính toán độc lập, không thay đổi bàn cờ gốc.
.  [PASS] UC-05 Main Flow: testStep5_1_LuongPVE_HoanChinh - Luồng PVE hoàn chỉnh: Player → AI (500ms delay) → Player.
.  [PASS] UC-05 Main Flow: testStep5_1_5_HieuNangMinimaxAlphaBeta - Thời gian tính toán đáp ứng yêu cầu tất cả độ khó.

Time: 0.973

OK (5 tests)
*/

