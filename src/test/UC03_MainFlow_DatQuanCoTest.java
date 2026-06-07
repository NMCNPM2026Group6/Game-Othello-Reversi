package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;

/**
 * ===================================================================
 * UC-03: ĐẶT QUÂN CỜ — KIỂM THỬ LUỒNG CHÍNH (Main Flow)
 * ===================================================================
 * Tham chiếu: flow/Uc-03/Main Flow.txt
 * 
 * Luồng chính mô tả quy trình đặt quân cờ hợp lệ:
 *   3.1.0. View highlight các ô hợp lệ bằng màu xanh nhạt
 *   3.1.1. Player click vào ô trên bàn cờ
 *   3.1.2. View gửi ActionEvent tới Controller (parse row, col)
 *   3.1.3. Controller gọi model.DatQuanCo(row, col)
 *   3.1.4. Model kiểm tra NuocDiHopLe() → true
 *   3.1.5. Đặt quân: board[row][col] = LuotChoiHienTai
 *   3.1.6. Lật các quân cờ bị kẹp: latCacQuanCo(row, col)
 *   3.1.7. Đổi lượt chơi: DoiLuot()
 *   3.1.8. Cập nhật điểm số: updateScore()
 *   3.1.9. Cập nhật giao diện: view.updateView(...)
 *   3.1.10. Xử lý lượt tiếp theo: XuLyLuotTiepTheo()
 *
 * Các test case bao phủ:
 *   - Bước 3.1.4: Kiểm tra nước đi hợp lệ trả về true
 *   - Bước 3.1.5: Quân cờ được đặt đúng vị trí trên board
 *   - Bước 3.1.6: Lật quân đúng hướng (8 hướng: E, W, S, N, SE, NW, NE, SW)
 *   - Bước 3.1.6: Lật nhiều quân trên cùng một đường thẳng
 *   - Bước 3.1.6: Lật quân đồng thời từ nhiều hướng khác nhau
 *   - Bước 3.1.7: Đổi lượt sau khi đặt quân thành công
 *   - Bước 3.1.8: Điểm số được cập nhật chính xác
 */
public class UC03_MainFlow_DatQuanCoTest {
    private ReversiModel model;
    private int[][] board;

    @Before
    public void setUp() {
        model = new ReversiModel();
        board = model.getBoard();
        // Xoá toàn bộ bàn cờ để thiết lập từng kịch bản riêng biệt
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;
    }

    // =====================================================================
    // Bước 3.1.4 + 3.1.5 + 3.1.6: Lật quân theo hướng ĐÔNG (East)
    // Thiết lập: Đen(3,2), Trắng(3,3) → Đặt Đen tại (3,4) → Trắng(3,3) bị lật
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongDong() {
        board[3][2] = ReversiModel.BLACK;
        board[3][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 4);

        assertTrue("3.1.4: DatQuanCo() phải trả về true khi nước đi hợp lệ", ketQua);
        assertEquals("3.1.5: Quân Đen được đặt tại (3,4)", ReversiModel.BLACK, board[3][4]);
        assertEquals("3.1.6: Quân Trắng tại (3,3) bị lật thành Đen", ReversiModel.BLACK, board[3][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDong - Lật quân hướng Đông thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng TÂY (West)
    // Thiết lập: Đen(3,5), Trắng(3,4) → Đặt Đen tại (3,3) → Trắng(3,4) bị lật
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongTay() {
        board[3][5] = ReversiModel.BLACK;
        board[3][4] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 3);

        assertTrue("3.1.4: DatQuanCo() trả về true", ketQua);
        assertEquals("3.1.6: Quân Trắng tại (3,4) bị lật thành Đen", ReversiModel.BLACK, board[3][4]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTay - Lật quân hướng Tây thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng NAM (South)
    // Thiết lập: Đen(2,3), Trắng(3,3) → Đặt Đen tại (4,3) → Trắng(3,3) bị lật
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongNam() {
        board[2][3] = ReversiModel.BLACK;
        board[3][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(4, 3);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[3][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongNam - Lật quân hướng Nam thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng BẮC (North)
    // Thiết lập: Đen(5,3), Trắng(4,3) → Đặt Đen tại (3,3) → Trắng(4,3) bị lật
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongBac() {
        board[5][3] = ReversiModel.BLACK;
        board[4][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 3);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[4][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongBac - Lật quân hướng Bắc thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng ĐÔNG NAM (Southeast)
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongDongNam() {
        board[2][2] = ReversiModel.BLACK;
        board[3][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(4, 4);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[3][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDongNam - Lật quân hướng Đông Nam thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng TÂY BẮC (Northwest)
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongTayBac() {
        board[5][5] = ReversiModel.BLACK;
        board[4][4] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 3);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[4][4]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTayBac - Lật quân hướng Tây Bắc thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng ĐÔNG BẮC (Northeast)
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongDongBac() {
        board[5][2] = ReversiModel.BLACK;
        board[4][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 4);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[4][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDongBac - Lật quân hướng Đông Bắc thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân theo hướng TÂY NAM (Southwest)
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanHuongTayNam() {
        board[2][5] = ReversiModel.BLACK;
        board[3][4] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(4, 3);

        assertTrue(ketQua);
        assertEquals(ReversiModel.BLACK, board[3][4]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTayNam - Lật quân hướng Tây Nam thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật NHIỀU QUÂN liên tiếp trên cùng một đường thẳng
    // Thiết lập: Đen(3,1), Trắng(3,2), Trắng(3,3), Trắng(3,4)
    //   → Đặt Đen tại (3,5) → 3 quân Trắng đều bị lật thành Đen
    // =====================================================================
    @Test
    public void testStep3_1_6_LatNhieuQuanLienTiep() {
        board[3][1] = ReversiModel.BLACK;
        board[3][2] = ReversiModel.WHITE;
        board[3][3] = ReversiModel.WHITE;
        board[3][4] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 5);

        assertTrue("DatQuanCo() phải thành công", ketQua);
        assertEquals("Quân (3,2) phải bị lật thành Đen", ReversiModel.BLACK, board[3][2]);
        assertEquals("Quân (3,3) phải bị lật thành Đen", ReversiModel.BLACK, board[3][3]);
        assertEquals("Quân (3,4) phải bị lật thành Đen", ReversiModel.BLACK, board[3][4]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatNhieuQuanLienTiep - Lật 3 quân liên tiếp trên cùng đường thẳng thành công.");
    }

    // =====================================================================
    // Bước 3.1.6: Lật quân từ NHIỀU HƯỚNG đồng thời trong một nước đi
    // Thiết lập: Đen(3,1) + Trắng(3,2) (hướng Tây)
    //            Đen(1,3) + Trắng(2,3) (hướng Bắc)
    //   → Đặt Đen tại (3,3) → Cả 2 quân Trắng đều bị lật
    // =====================================================================
    @Test
    public void testStep3_1_6_LatQuanNhieuHuongDongThoi() {
        board[3][1] = ReversiModel.BLACK;
        board[3][2] = ReversiModel.WHITE;
        board[1][3] = ReversiModel.BLACK;
        board[2][3] = ReversiModel.WHITE;

        boolean ketQua = model.DatQuanCo(3, 3);

        assertTrue(ketQua);
        assertEquals("Quân (3,2) bị lật theo hướng Tây", ReversiModel.BLACK, board[3][2]);
        assertEquals("Quân (2,3) bị lật theo hướng Bắc", ReversiModel.BLACK, board[2][3]);
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanNhieuHuongDongThoi - Lật quân đồng thời từ nhiều hướng thành công.");
    }

    // =====================================================================
    // Bước 3.1.7: Kiểm tra đổi lượt sau khi đặt quân thành công
    // Ban đầu lượt Đen → sau DatQuanCo() → chuyển sang lượt Trắng
    // =====================================================================
    @Test
    public void testStep3_1_7_DoiLuotSauKhiDat() {
        board[3][2] = ReversiModel.BLACK;
        board[3][3] = ReversiModel.WHITE;

        assertEquals("Ban đầu lượt Đen", ReversiModel.BLACK, model.getLuotChoiHienTai());

        model.DatQuanCo(3, 4);

        assertEquals("3.1.7: Sau khi đặt quân, lượt phải chuyển sang Trắng",
                ReversiModel.WHITE, model.getLuotChoiHienTai());
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_7_DoiLuotSauKhiDat - Đổi lượt từ Đen sang Trắng thành công.");
    }

    // =====================================================================
    // Bước 3.1.8: Kiểm tra cập nhật điểm số sau khi đặt quân
    // Bàn cờ khởi tạo chuẩn → Đen đặt tại (2,3) → lật (3,3)
    //   Đen: 4 quân, Trắng: 1 quân
    // =====================================================================
    @Test
    public void testStep3_1_8_CapNhatDiemSo() {
        // Khởi tạo bàn cờ chuẩn
        model.resetGame();

        assertEquals("Ban đầu Đen = 2", 2, model.getBlackScore());
        assertEquals("Ban đầu Trắng = 2", 2, model.getWhiteScore());

        model.DatQuanCo(2, 3); // Đen đặt tại (2,3), lật (3,3) thành Đen

        assertEquals("3.1.8: Sau nước đi, Đen = 4", 4, model.getBlackScore());
        assertEquals("3.1.8: Sau nước đi, Trắng = 1", 1, model.getWhiteScore());
        System.out.println("  [PASS] UC-03 Main Flow: testStep3_1_8_CapNhatDiemSo - Điểm số được cập nhật chính xác (Đen: 4, Trắng: 1).");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC03_MainFlow_DatQuanCoTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-03 Main Flow: testStep3_1_7_DoiLuotSauKhiDat - Đổi lượt từ Đen sang Trắng thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTayBac - Lật quân hướng Tây Bắc thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTayNam - Lật quân hướng Tây Nam thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDongBac - Lật quân hướng Đông Bắc thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDongNam - Lật quân hướng Đông Nam thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_8_CapNhatDiemSo - Điểm số được cập nhật chính xác (Đen: 4, Trắng: 1).
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatNhieuQuanLienTiep - Lật 3 quân liên tiếp trên cùng đường thẳng thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongBac - Lật quân hướng Bắc thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongNam - Lật quân hướng Nam thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongTay - Lật quân hướng Tây thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanHuongDong - Lật quân hướng Đông thành công.
.  [PASS] UC-03 Main Flow: testStep3_1_6_LatQuanNhieuHuongDongThoi - Lật quân đồng thời từ nhiều hướng thành công.

Time: 0.004

OK (12 tests)
*/

