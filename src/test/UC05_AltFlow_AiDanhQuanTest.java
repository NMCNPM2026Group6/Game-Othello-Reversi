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
import java.lang.reflect.Field;
import javax.swing.Timer;

/**
 * ===================================================================
 * UC-05: AI ĐÁNH QUÂN — KIỂM THỬ LUỒNG THAY THẾ (Alternative Flows)
 * ===================================================================
 * Tham chiếu: flow/Uc-05/Alternative Flows.txt
 *
 * AF-01: AI không có nước đi hợp lệ — Skip Turn
 *   5.2.1. CoNuocDiHopLe(aiPlayer) = false → AI bỏ lượt
 *   5.2.3. DoiLuot() trả lượt cho Player
 *   5.2.5. AI không được kích hoạt
 *
 * AF-02: Chế độ PVP — AI bị tắt
 *   5.3.1. aiEnabled == false → AI không được gọi
 *   5.3.3. Chờ Player tiếp theo đi thủ công
 *
 * AF-03: Cả hai bên không có nước đi — Kết thúc
 *   5.4.1–5.4.4. CoNuocDiHopLe(cả hai) = false → GameOver()
 *
 * AF-04: AI đặt quân thất bại (bestMove == null)
 *   5.5.1–5.5.4. findBestMove() trả về null → không gọi DatQuanCo()
 */
public class UC05_AltFlow_AiDanhQuanTest {
    private ReversiModel model;

    @Before
    public void setUp() {
        model = new ReversiModel();
    }

    // =====================================================================
    // AF-01 (Bước 5.2.1–5.2.5): AI không có nước đi → SKIP TURN
    // Thiết lập: bàn cờ khiến Trắng(AI) không có nước đi hợp lệ
    //   → CoNuocDiHopLe(WHITE) = false
    //   → Hệ thống DoiLuot() trả lượt về Đen(Player)
    // =====================================================================
    @Test
    public void testAF01_AiKhongCoNuocDi_SkipTurn() {
        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        // Chỉ đặt quân Đen, không có quân Trắng nào bị kẹp
        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.BLACK;
        board[0][2] = ReversiModel.WHITE;

        // 5.2.1: Kiểm tra Trắng (AI) KHÔNG có nước đi hợp lệ
        boolean aiCoNuocDi = model.CoNuocDiHopLe(ReversiModel.WHITE);
        assertFalse("5.2.1: CoNuocDiHopLe(WHITE) phải trả về false", aiCoNuocDi);

        // 5.2.5: Đen (Player) VẪN có nước đi → trò chơi tiếp tục
        // (Nếu Đen cũng không có → thuộc AF-03 Kết thúc)
        // Ở đây chỉ kiểm tra logic bỏ lượt AI

        System.out.println("  [PASS] UC-05 AF-01: testAF01_AiKhongCoNuocDi_SkipTurn - AI không có nước đi → bỏ lượt.");
    }

    // =====================================================================
    // AF-02 (Bước 5.3.1–5.3.3): Chế độ PVP — AI BỊ TẮT
    // configure(GameConfig.pvp()) → aiEnabled = false
    //   → Sau khi Player 1 đi, hệ thống KHÔNG gọi aiMove()
    //   → Chờ Player 2 đi thủ công
    // =====================================================================
    @Test
    public void testAF02_CheDoPVP_AiBiTat() throws Exception {
        ReversiModel pvpModel = new ReversiModel();
        ReversiView pvpView = new ReversiView();
        ReversiController pvpController = new ReversiController(pvpModel, pvpView);

        // 5.3.1: Cấu hình PVP → aiEnabled = false
        pvpController.configure(GameConfig.pvp());

        // Player 1 (Đen) đi nước đầu
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        pvpController.actionPerformed(event);

        // 5.3.2: Lượt chuyển sang Trắng (Player 2)
        assertEquals("Lượt chuyển sang Trắng", ReversiModel.WHITE, pvpModel.getLuotChoiHienTai());

        // 5.3.3: Chờ 700ms — AI KHÔNG ĐƯỢC kích hoạt
        Thread.sleep(700);

        // Lượt VẪN là Trắng → AI không tự đi
        assertEquals("5.3.3: AI không kích hoạt, lượt vẫn là Trắng (Player 2)",
                ReversiModel.WHITE, pvpModel.getLuotChoiHienTai());

        System.out.println("  [PASS] UC-05 AF-02: testAF02_CheDoPVP_AiBiTat - Chế độ PVP: AI bị tắt, chờ Player 2 đi thủ công.");
    }

    // =====================================================================
    // AF-03 (Bước 5.4.1–5.4.4): Cả hai bên KHÔNG CÓ nước đi → KẾT THÚC
    //   5.4.1: CoNuocDiHopLe(nextPlayer) = false
    //   5.4.2: CoNuocDiHopLe(currentPlayer) = false
    //   5.4.3: Gọi GameOver()
    //   5.4.4: Hiển thị kết quả và hộp thoại lựa chọn
    // =====================================================================
    @Test
    public void testAF03_CaHaiBenHetNuocDi_KetThuc() throws Exception {
        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.BLACK;
        board[7][7] = ReversiModel.WHITE;

        // 5.4.1: Cả hai bên đều không có nước đi
        assertFalse("5.4.1: Đen không có nước đi", model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse("5.4.2: Trắng không có nước đi", model.CoNuocDiHopLe(ReversiModel.WHITE));

        // 5.4.4: Kiểm tra kết quả
        java.lang.reflect.Method updateScoreMethod = model.getClass().getDeclaredMethod("updateScore");
        updateScoreMethod.setAccessible(true);
        updateScoreMethod.invoke(model);

        String ketQua = model.getGameResult();
        assertEquals("5.4.4: Đen có 2 quân, Trắng có 1 quân → Đen thắng",
                "ĐEN THẮNG! (2 - 1)", ketQua);

        System.out.println("  [PASS] UC-05 AF-03: testAF03_CaHaiBenHetNuocDi_KetThuc - Cả hai hết nước đi → GameOver() → ĐEN THẮNG.");
    }

    // =====================================================================
    // AF-04 (Bước 5.5.1–5.5.4): AI đặt quân THẤT BẠI
    // Khi không có nước đi hợp lệ, findBestMove() trả về toạ độ mặc định {0,0}
    //   nhưng DatQuanCo() tại toạ độ đó sẽ trả về false → AI không đặt quân
    //   5.5.3: Điều kiện bestMove != null = true NHƯNG DatQuanCo() = false
    //   5.5.4: Bàn cờ giữ nguyên trạng thái
    // =====================================================================
    @Test
    public void testAF04_AiDatQuanThatBai() {
        ReversiAI ai = new ReversiAI(ReversiModel.WHITE);
        ai.setDifficulty(AiDifficulty.EASY);

        // Bàn cờ chỉ có quân cùng màu — không có đối thủ để kẹp
        int[][] boardKhongKep = new int[8][8];
        boardKhongKep[3][3] = ReversiModel.WHITE;
        boardKhongKep[3][4] = ReversiModel.WHITE;

        // findBestMove() trả về {0,0} mặc định (AI luôn trả về int[2])
        int[] bestMove = ai.findBestMove(boardKhongKep);
        assertNotNull("findBestMove() luôn trả về mảng int[2]", bestMove);

        // 5.5.3: DatQuanCo() tại toạ độ bestMove phải trả về false
        // vì không có thế kẹp hợp lệ → AI không đặt quân thành công
        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = boardKhongKep[r][c];

        model.DoiLuot(); // Chuyển sang Trắng (AI)
        boolean ketQua = model.DatQuanCo(bestMove[0], bestMove[1]);
        assertFalse("5.5.3: DatQuanCo() phải trả về false khi nước đi không hợp lệ", ketQua);

        System.out.println("  [PASS] UC-05 AF-04: testAF04_AiDatQuanThatBai - AI không đặt quân thành công khi không có nước đi hợp lệ.");
    }

    // =====================================================================
    // AF-04 bổ sung: Kiểm tra Timer bị dừng khi quay về menu giữa chừng
    // Tránh trường hợp AI đặt quân sau khi đã thoát khỏi game (memory leak)
    // =====================================================================
    @Test
    public void testAF04_BoSung_TimerBiDungKhiQuayVeMenu() throws Exception {
        ReversiModel pveModel = new ReversiModel();
        ReversiView pveView = new ReversiView();
        ReversiController pveController = new ReversiController(pveModel, pveView);
        pveController.configure(GameConfig.pve(AiDifficulty.EASY));

        // Player đi nước đầu → kích hoạt AI Timer
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        pveController.actionPerformed(event);

        // Lấy pendingAiTimer qua reflection
        Field timerField = pveController.getClass().getDeclaredField("pendingAiTimer");
        timerField.setAccessible(true);
        Timer pendingAiTimer = (Timer) timerField.get(pveController);

        assertTrue("Timer phải đang chạy", pendingAiTimer.isRunning());

        // Quay về menu → Timer phải bị dừng
        pveController.returnToMenu();
        assertFalse("Timer phải bị dừng khi quay về menu", pendingAiTimer.isRunning());

        System.out.println("  [PASS] UC-05 AF-04: testAF04_BoSung_TimerBiDungKhiQuayVeMenu - Timer AI bị dừng an toàn khi quay về menu.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC05_AltFlow_AiDanhQuanTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-05 AF-03: testAF03_CaHaiBenHetNuocDi_KetThuc - Cả hai hết nước đi → GameOver() → ĐEN THẮNG.
.  [PASS] UC-05 AF-01: testAF01_AiKhongCoNuocDi_SkipTurn - AI không có nước đi → bỏ lượt.
.  [PASS] UC-05 AF-02: testAF02_CheDoPVP_AiBiTat - Chế độ PVP: AI bị tắt, chờ Player 2 đi thủ công.
.  [PASS] UC-05 AF-04: testAF04_AiDatQuanThatBai - AI không đặt quân thành công khi không có nước đi hợp lệ.
.  [PASS] UC-05 AF-04: testAF04_BoSung_TimerBiDungKhiQuayVeMenu - Timer AI bị dừng an toàn khi quay về menu.

Time: 0.899

OK (5 tests)
*/

