package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;

/**
 * ===================================================================
 * UC-08: KẾT THÚC GAME — KIỂM THỬ LUỒNG CHÍNH (Main Flow)
 * ===================================================================
 * Tham chiếu: flow/Uc-08/Main Flow.txt
 *
 * Luồng chính mô tả quy trình kết thúc game:
 *   8.1.0. Sau DatQuanCo() thành công → gọi XuLyLuotTiepTheo()
 *   8.1.1. CoNuocDiHopLe(LuotTiepTheo) → false
 *   8.1.2. Hiển thị thông báo bỏ lượt
 *   8.1.3. DoiLuot() + CoNuocDiHopLe(LuotBanDau) → false
 *   8.1.4. Xác nhận cả hai bên hết nước đi → GameOver()
 *   8.1.5. getGameResult() → "ĐEN THẮNG!", "TRẮNG THẮNG!", "HÒA!"
 *   8.1.6. showMessage("TRÒ CHƠI KẾT THÚC!\n" + result)
 *   8.1.7. JOptionPane hiển thị 3 lựa chọn: Chơi lại / Về Menu / Thoát
 *
 * Các test case bao phủ:
 *   - Bước 8.1.1+8.1.3: Cả hai bên không có nước đi
 *   - Bước 8.1.5: Kết quả Đen thắng / Trắng thắng / Hòa
 *   - Bước 8.1.4: Bàn cờ đầy → cả hai không đi được
 */
public class UC08_MainFlow_KetThucGameTest {
    private ReversiModel model;
    private int[][] board;

    @Before
    public void setUp() {
        model = new ReversiModel();
        board = model.getBoard();
        // Xoá toàn bộ bàn cờ để thiết lập kịch bản
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;
    }

    /** Gọi updateScore() qua reflection để cập nhật điểm */
    private void capNhatDiem() throws Exception {
        java.lang.reflect.Method m = model.getClass().getDeclaredMethod("updateScore");
        m.setAccessible(true);
        m.invoke(model);
    }

    // =====================================================================
    // Bước 8.1.1 + 8.1.3 + 8.1.5: ĐEN THẮNG
    // Thiết lập: Đen 3 quân, Trắng 1 quân → cả hai không có nước đi
    //   → getGameResult() = "ĐEN THẮNG! (3 - 1)"
    // =====================================================================
    @Test
    public void testStep8_1_5_DenThang() throws Exception {
        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.BLACK;
        board[0][2] = ReversiModel.BLACK;
        board[7][7] = ReversiModel.WHITE;

        capNhatDiem();

        // 8.1.1: Đen không có nước đi hợp lệ
        assertFalse("8.1.1: Đen không có nước đi", model.CoNuocDiHopLe(ReversiModel.BLACK));
        // 8.1.3: Trắng cũng không có nước đi hợp lệ
        assertFalse("8.1.3: Trắng không có nước đi", model.CoNuocDiHopLe(ReversiModel.WHITE));

        // 8.1.5: Kết quả phải là "ĐEN THẮNG!"
        assertEquals("8.1.5: Đen 3 > Trắng 1 → ĐEN THẮNG!",
                "ĐEN THẮNG! (3 - 1)", model.getGameResult());
        assertEquals("Điểm Đen = 3", 3, model.getBlackScore());
        assertEquals("Điểm Trắng = 1", 1, model.getWhiteScore());

        System.out.println("  [PASS] UC-08 Main Flow: testStep8_1_5_DenThang - Kết quả ĐEN THẮNG! (3 - 1) hiển thị chính xác.");
    }

    // =====================================================================
    // Bước 8.1.5: TRẮNG THẮNG
    // Thiết lập: Đen 1 quân, Trắng 3 quân
    //   → getGameResult() = "TRẮNG THẮNG! (3 - 1)"
    // =====================================================================
    @Test
    public void testStep8_1_5_TrangThang() throws Exception {
        board[0][0] = ReversiModel.BLACK;
        board[7][5] = ReversiModel.WHITE;
        board[7][6] = ReversiModel.WHITE;
        board[7][7] = ReversiModel.WHITE;

        capNhatDiem();

        assertFalse(model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse(model.CoNuocDiHopLe(ReversiModel.WHITE));
        assertEquals("8.1.5: Trắng 3 > Đen 1 → TRẮNG THẮNG!",
                "TRẮNG THẮNG! (3 - 1)", model.getGameResult());

        System.out.println("  [PASS] UC-08 Main Flow: testStep8_1_5_TrangThang - Kết quả TRẮNG THẮNG! (3 - 1) hiển thị chính xác.");
    }

    // =====================================================================
    // Bước 8.1.5: HÒA
    // Thiết lập: Đen 2 quân, Trắng 2 quân
    //   → getGameResult() = "HÒA! (2 - 2)"
    // =====================================================================
    @Test
    public void testStep8_1_5_Hoa() throws Exception {
        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.BLACK;
        board[7][6] = ReversiModel.WHITE;
        board[7][7] = ReversiModel.WHITE;

        capNhatDiem();

        assertFalse(model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse(model.CoNuocDiHopLe(ReversiModel.WHITE));
        assertEquals("8.1.5: Đen 2 = Trắng 2 → HÒA!", "HÒA! (2 - 2)", model.getGameResult());

        System.out.println("  [PASS] UC-08 Main Flow: testStep8_1_5_Hoa - Kết quả HÒA! (2 - 2) hiển thị chính xác.");
    }

    // =====================================================================
    // Bước 8.1.4: BÀN CỜ ĐẦY → Cả hai không đi được → GameOver()
    // Điền xen kẽ Đen/Trắng toàn bàn → không ô trống → không ai đi được
    // =====================================================================
    @Test
    public void testStep8_1_4_BanCoDay() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ((r + c) % 2 == 0) ? ReversiModel.BLACK : ReversiModel.WHITE;

        // Không ai có nước đi (bàn cờ đầy)
        assertFalse("8.1.4: Đen không đi được khi bàn cờ đầy",
                model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse("8.1.4: Trắng không đi được khi bàn cờ đầy",
                model.CoNuocDiHopLe(ReversiModel.WHITE));

        System.out.println("  [PASS] UC-08 Main Flow: testStep8_1_4_BanCoDay - Bàn cờ đầy → cả hai bên không đi được → GameOver().");
    }

    // =====================================================================
    // Bước 8.1.1 + 8.1.3: Bỏ lượt LIÊN TIẾP dẫn đến kết thúc game
    // Cả Đen và Trắng đều cô lập → CoNuocDiHopLe() trả false cho cả hai
    // =====================================================================
    @Test
    public void testStep8_1_BoLuotLienTiep() throws Exception {
        board[0][0] = ReversiModel.BLACK;
        board[7][7] = ReversiModel.WHITE;

        capNhatDiem();

        // 8.1.1: Lượt tiếp theo không có nước đi
        assertFalse("8.1.1: Lượt tiếp không có nước đi",
                model.CoNuocDiHopLe(ReversiModel.WHITE));
        // 8.1.3: Lượt ban đầu cũng không có nước đi
        assertFalse("8.1.3: Lượt ban đầu cũng không có nước đi",
                model.CoNuocDiHopLe(ReversiModel.BLACK));
        // 8.1.5: Kết quả
        assertEquals("Kết quả HÒA vì mỗi bên 1 quân", "HÒA! (1 - 1)", model.getGameResult());

        System.out.println("  [PASS] UC-08 Main Flow: testStep8_1_BoLuotLienTiep - Bỏ lượt liên tiếp → GameOver() → HÒA.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC08_MainFlow_KetThucGameTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-08 Main Flow: testStep8_1_4_BanCoDay - Bàn cờ đầy → cả hai bên không đi được → GameOver().
.  [PASS] UC-08 Main Flow: testStep8_1_5_TrangThang - Kết quả TRẮNG THẮNG! (3 - 1) hiển thị chính xác.
.  [PASS] UC-08 Main Flow: testStep8_1_5_DenThang - Kết quả ĐEN THẮNG! (3 - 1) hiển thị chính xác.
.  [PASS] UC-08 Main Flow: testStep8_1_BoLuotLienTiep - Bỏ lượt liên tiếp → GameOver() → HÒA.
.  [PASS] UC-08 Main Flow: testStep8_1_5_Hoa - Kết quả HÒA! (2 - 2) hiển thị chính xác.

Time: 0.008

OK (5 tests)
*/

