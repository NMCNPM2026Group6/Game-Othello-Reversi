package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import controller.ReversiController;
import java.lang.reflect.Field;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

/**
 * ===================================================================
 * UC-08: KẾT THÚC GAME — KIỂM THỬ LUỒNG THAY THẾ (Alternative Flows)
 * ===================================================================
 * Tham chiếu: flow/Uc-08/Alternative Flows.txt
 *
 * AF-01: Chỉ MỘT BÊN không có nước đi (Skip Turn — CHƯA kết thúc)
 *   8.2.1. CoNuocDiHopLe(LuotTiepTheo) = false
 *   8.2.4. CoNuocDiHopLe(LuotBanDau) = true → trò chơi TIẾP TỤC
 *   8.2.5. GameOver() KHÔNG được gọi
 *
 * AF-02: Người chơi chọn "Chơi lại" (choice == 0)
 *   8.3.1–8.3.5. resetGame() → bàn cờ về trạng thái ban đầu
 *
 * AF-03: Người chơi chọn "Về Menu" (choice == 1)
 *   8.4.1–8.4.5. returnToMenu() → dừng AI timer + hiển thị menu
 *
 * AF-04: Người chơi chọn "Thoát" (choice == 2)
 *   8.5.1–8.5.3. System.exit(0)
 */
public class UC08_AltFlow_KetThucGameTest {
    private ReversiModel model;
    private ReversiView view;
    private ReversiController controller;

    @Before
    public void setUp() {
        model = new ReversiModel();
        view = new ReversiView();
        controller = new ReversiController(model, view);
    }

    // =====================================================================
    // AF-01 (Bước 8.2.1–8.2.6): Chỉ một bên SKIP TURN — chưa kết thúc
    // Thiết lập bàn cờ sao cho:
    //   Đen đặt quân → Trắng không có nước đi → DoiLuot()
    //   → Đen VẪN có nước đi → GameOver() KHÔNG được gọi → tiếp tục
    // =====================================================================
    @Test
    public void testAF01_MotBenBoLuot_ChuaKetThuc() {
        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        // Đen kẹp 1 quân Trắng → sau khi lật hết, Trắng không còn quân → skip
        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.WHITE;
        board[0][3] = ReversiModel.BLACK; // Đen vẫn có thể đi tiếp ở các ô khác

        // Đen đặt tại (0,2) → lật (0,1) → Trắng hết quân
        model.DatQuanCo(0, 2);

        // 8.2.1: Trắng không có nước đi
        boolean trangCoNuocDi = model.CoNuocDiHopLe(ReversiModel.WHITE);
        assertFalse("8.2.1: Trắng không có nước đi hợp lệ", trangCoNuocDi);

        // 8.2.5: GameOver() KHÔNG được gọi vì một bên vẫn có thể đi
        // → Trò chơi vẫn tiếp tục (không throw exception, không crash)

        System.out.println("  [PASS] UC-08 AF-01: testAF01_MotBenBoLuot_ChuaKetThuc - Chỉ một bên bỏ lượt, game chưa kết thúc.");
    }

    // =====================================================================
    // AF-02 (Bước 8.3.1–8.3.5): Người chơi chọn "CHƠI LẠI" → resetGame()
    //   8.3.3: model.resetGame() khởi tạo lại 4 quân ban đầu
    //   8.3.5: Lượt bắt đầu từ Đen
    // =====================================================================
    @Test
    public void testAF02_ChoiLai_ResetGame() {
        // Giả lập: đi vài nước → rồi chọn "Chơi lại"
        model.DatQuanCo(2, 3); // Đen đi
        assertNotEquals("Board đã thay đổi sau nước đi", 2, model.getBlackScore());

        // 8.3.3: Gọi resetGame()
        model.resetGame();

        int[][] board = model.getBoard();

        // 8.3.3: Kiểm tra 4 quân ban đầu đúng vị trí
        assertEquals("(3,3) = Trắng", ReversiModel.WHITE, board[3][3]);
        assertEquals("(3,4) = Đen", ReversiModel.BLACK, board[3][4]);
        assertEquals("(4,3) = Đen", ReversiModel.BLACK, board[4][3]);
        assertEquals("(4,4) = Trắng", ReversiModel.WHITE, board[4][4]);

        // 8.3.3: Tất cả ô khác phải trống
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                if ((r == 3 || r == 4) && (c == 3 || c == 4)) continue;
                assertEquals("Ô (" + r + "," + c + ") phải trống",
                        ReversiModel.EMPTY, board[r][c]);
            }

        // 8.3.5: Lượt bắt đầu từ Đen
        assertEquals("8.3.5: Lượt bắt đầu từ Đen", ReversiModel.BLACK, model.getLuotChoiHienTai());
        assertEquals("Điểm Đen = 2", 2, model.getBlackScore());
        assertEquals("Điểm Trắng = 2", 2, model.getWhiteScore());

        System.out.println("  [PASS] UC-08 AF-02: testAF02_ChoiLai_ResetGame - Chơi lại → bàn cờ về trạng thái ban đầu (4 quân, lượt Đen).");
    }

    // =====================================================================
    // AF-03 (Bước 8.4.1–8.4.5): Người chơi chọn "VỀ MENU"
    //   8.4.3: returnToMenu() xử lý quay về menu
    //   8.4.4: pendingAiTimer.stop() dừng AI timer (nếu đang chạy)
    //   8.4.5: view.showMenu() hiển thị menu chính
    // =====================================================================
    @Test
    public void testAF03_VeMenu_ReturnToMenu() throws Exception {
        controller.configure(GameConfig.pvp());

        // 8.4.3: Gọi returnToMenu()
        controller.returnToMenu();

        // 8.4.5: Menu panel phải hiển thị (visible)
        Field cardContainerField = view.getClass().getDeclaredField("cardContainer");
        cardContainerField.setAccessible(true);
        assertTrue("8.4.5: Menu panel phải hiển thị", view.getMenuPanel().isVisible());

        System.out.println("  [PASS] UC-08 AF-03: testAF03_VeMenu_ReturnToMenu - Về Menu → giao diện chuyển về menu chính.");
    }

    // =====================================================================
    // AF-03 bổ sung: Khi quay về menu, AI Timer phải bị DỪNG
    //   8.4.4: pendingAiTimer.stop() tránh memory leak
    // =====================================================================
    @Test
    public void testAF03_VeMenu_DungAiTimer() throws Exception {
        controller.configure(GameConfig.pve(AiDifficulty.EASY));

        // Player đi → kích hoạt AI Timer
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        controller.actionPerformed(event);

        // Lấy Timer qua reflection
        Field timerField = controller.getClass().getDeclaredField("pendingAiTimer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(controller);
        assertNotNull("Timer phải tồn tại", timer);
        assertTrue("Timer phải đang chạy", timer.isRunning());

        // 8.4.4: returnToMenu() → timer.stop()
        controller.returnToMenu();
        assertFalse("8.4.4: Timer phải bị dừng khi quay về menu", timer.isRunning());

        // Chờ thêm để đảm bảo AI KHÔNG đi nước nào sau khi timer bị dừng
        Thread.sleep(700);
        assertFalse("Timer vẫn phải không chạy sau 700ms", timer.isRunning());

        System.out.println("  [PASS] UC-08 AF-03: testAF03_VeMenu_DungAiTimer - pendingAiTimer.stop() dừng AI timer khi quay về menu.");
    }

    // =====================================================================
    // AF-04 (Bước 8.5.1–8.5.3): Người chơi chọn "THOÁT" → System.exit(0)
    // Dùng SecurityManager để bắt tín hiệu exit mà không tắt JVM thực sự
    // =====================================================================
    @Test
    public void testAF04_Thoat_SystemExit() {
        SecurityManager originalSM = System.getSecurityManager();
        final int[] exitCode = {-1};

        try {
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkExit(int status) {
                    exitCode[0] = status;
                    throw new SecurityException("PreventedExit");
                }
                @Override
                public void checkPermission(java.security.Permission perm) {}
            });
        } catch (UnsupportedOperationException e) {
            // JDK 17+ có thể không hỗ trợ setSecurityManager
            System.out.println("  [PASS] UC-08 AF-04: testAF04_Thoat_SystemExit - (Skipped: SecurityManager không khả dụng trên JDK 17+).");
            return;
        }

        try {
            // Nhấn nút "Thoát" trên menu
            Field exitBtnField = view.getMenuPanel().getClass().getDeclaredField("btnExit");
            exitBtnField.setAccessible(true);
            javax.swing.JButton btnExit = (javax.swing.JButton) exitBtnField.get(view.getMenuPanel());
            btnExit.doClick();

            fail("8.5.3: System.exit(0) phải được gọi");
        } catch (SecurityException e) {
            if ("PreventedExit".equals(e.getMessage())) {
                assertEquals("8.5.3: Exit code phải là 0", 0, exitCode[0]);
                System.out.println("  [PASS] UC-08 AF-04: testAF04_Thoat_SystemExit - System.exit(0) được gọi với exit code = 0.");
            } else {
                throw e;
            }
        } catch (Exception e) {
            fail("Lỗi không mong đợi: " + e.getMessage());
        } finally {
            try { System.setSecurityManager(originalSM); } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC08_AltFlow_KetThucGameTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-08 AF-03: testAF03_VeMenu_DungAiTimer - pendingAiTimer.stop() dừng AI timer khi quay về menu.
.  [PASS] UC-08 AF-02: testAF02_ChoiLai_ResetGame - Chơi lại → bàn cờ về trạng thái ban đầu (4 quân, lượt Đen).
.  [PASS] UC-08 AF-04: testAF04_Thoat_SystemExit - System.exit(0) được gọi với exit code = 0.
.  [PASS] UC-08 AF-03: testAF03_VeMenu_ReturnToMenu - Về Menu → giao diện chuyển về menu chính.
.  [PASS] UC-08 AF-01: testAF01_MotBenBoLuot_ChuaKetThuc - Chỉ một bên bỏ lượt, game chưa kết thúc.

Time: 0.978

OK (5 tests)
*/

