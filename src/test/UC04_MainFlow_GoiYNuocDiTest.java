package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;
import view.ReversiView;
import java.lang.reflect.Field;
import java.awt.Color;
import javax.swing.JButton;

/**
 * ===================================================================
 * UC-04: XEM GỢI Ý NƯỚC ĐI — KIỂM THỬ LUỒNG CHÍNH (Main Flow)
 * ===================================================================
 * Tham chiếu: flow/Uc-04/Main Flow.txt
 *
 * Luồng chính mô tả quy trình hiển thị gợi ý nước đi hợp lệ:
 *   4.1.0. Sau khi DatQuanCo() thành công → Controller gọi updateViewFromModel()
 *   4.1.1. Controller gọi updateViewFromModel()
 *   4.1.2. Controller gọi model.getValidMoves(model.getLuotChoiHienTai())
 *   4.1.3. Model duyệt toàn bộ 8×8, gọi NuocDiHopLe() cho từng ô
 *   4.1.4. Model trả về boolean[][] validMoves
 *   4.1.5. Controller gọi view.updateView(board, player, scores, validMoves)
 *   4.1.6. View: validMoves[i][j] == true → cells[i][j].setBackground(new Color(200,255,200))
 *   4.1.7. View: validMoves[i][j] == false → cells[i][j].setBackground(Color.LIGHT_GRAY)
 *
 * Các test case bao phủ:
 *   - Bước 4.1.2–4.1.4: getValidMoves() trả về đúng 4 ô hợp lệ ban đầu
 *   - Bước 4.1.6: Ô hợp lệ được highlight bằng màu xanh nhạt RGB(200,255,200)
 *   - Bước 4.1.7: Ô không hợp lệ giữ nguyên Color.LIGHT_GRAY
 *   - Sau khi đặt quân, gợi ý được cập nhật cho lượt mới
 */
public class UC04_MainFlow_GoiYNuocDiTest {
    private ReversiModel model;
    private ReversiView view;

    /** Màu xanh nhạt dùng để highlight ô hợp lệ (theo flow 4.1.6) */
    private static final Color MAU_GOI_Y = new Color(200, 255, 200);

    @Before
    public void setUp() {
        model = new ReversiModel();
        view = new ReversiView();
    }

    /** Lấy mảng cells[][] thông qua Reflection */
    private JButton[][] getCells() throws Exception {
        Field cellsField = view.getClass().getDeclaredField("cells");
        cellsField.setAccessible(true);
        return (JButton[][]) cellsField.get(view);
    }

    // =====================================================================
    // Bước 4.1.2–4.1.4: getValidMoves(BLACK) trả về đúng 4 ô hợp lệ ban đầu
    // Trên bàn cờ khởi tạo chuẩn, Đen có 4 nước đi: (2,3), (3,2), (4,5), (5,4)
    // =====================================================================
    @Test
    public void testStep4_1_3_GetValidMovesBanDau() {
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);

        assertTrue("(2,3) phải là nước đi hợp lệ", validMoves[2][3]);
        assertTrue("(3,2) phải là nước đi hợp lệ", validMoves[3][2]);
        assertTrue("(4,5) phải là nước đi hợp lệ", validMoves[4][5]);
        assertTrue("(5,4) phải là nước đi hợp lệ", validMoves[5][4]);
        System.out.println("  [PASS] UC-04 Main Flow: testStep4_1_3_GetValidMovesBanDau - 4 ô hợp lệ ban đầu cho Đen chính xác.");
    }

    // =====================================================================
    // Bước 4.1.6 + 4.1.7: View highlight đúng ô hợp lệ và không highlight ô sai
    // =====================================================================
    @Test
    public void testStep4_1_6_HighlightOchoHopLe() throws Exception {
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);

        JButton[][] cells = getCells();

        // 4.1.6: Ô hợp lệ phải có nền màu xanh nhạt
        assertEquals("4.1.6: Ô (2,3) highlight xanh nhạt", MAU_GOI_Y, cells[2][3].getBackground());
        assertEquals("4.1.6: Ô (3,2) highlight xanh nhạt", MAU_GOI_Y, cells[3][2].getBackground());
        assertEquals("4.1.6: Ô (4,5) highlight xanh nhạt", MAU_GOI_Y, cells[4][5].getBackground());
        assertEquals("4.1.6: Ô (5,4) highlight xanh nhạt", MAU_GOI_Y, cells[5][4].getBackground());

        // 4.1.7: Ô KHÔNG hợp lệ phải giữ nguyên màu LIGHT_GRAY
        assertEquals("4.1.7: Ô (0,0) không highlight, giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[0][0].getBackground());
        assertEquals("4.1.7: Ô (1,1) không highlight, giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[1][1].getBackground());

        System.out.println("  [PASS] UC-04 Main Flow: testStep4_1_6_HighlightOchoHopLe - Ô hợp lệ highlight xanh nhạt, ô không hợp lệ giữ LIGHT_GRAY.");
    }

    // =====================================================================
    // Bước 4.1.0: Sau khi DatQuanCo() → gợi ý cập nhật cho lượt mới (Trắng)
    // Đen đặt tại (2,3) → lượt chuyển sang Trắng → validMoves mới cho Trắng
    //   Ô (2,3) vừa được Đen đặt → KHÔNG còn highlight
    //   Ô (2,2) trở thành nước đi hợp lệ cho Trắng → highlight xanh nhạt
    // =====================================================================
    @Test
    public void testStep4_1_0_CapNhatGoiYSauDatQuan() throws Exception {
        // Đen đi nước đầu tiên
        model.DatQuanCo(2, 3);
        assertEquals("Lượt chuyển sang Trắng", ReversiModel.WHITE, model.getLuotChoiHienTai());

        boolean[][] validMovesWhite = model.getValidMoves(ReversiModel.WHITE);
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMovesWhite);

        JButton[][] cells = getCells();

        // Ô (2,2) trở thành hợp lệ cho Trắng
        assertTrue("(2,2) hợp lệ cho Trắng", validMovesWhite[2][2]);
        assertEquals("(2,2) được highlight xanh nhạt", MAU_GOI_Y, cells[2][2].getBackground());

        // Ô (2,3) vừa bị Đen đặt → KHÔNG còn highlight
        assertEquals("(2,3) giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[2][3].getBackground());

        System.out.println("  [PASS] UC-04 Main Flow: testStep4_1_0_CapNhatGoiYSauDatQuan - Gợi ý cập nhật chính xác sau khi chuyển lượt.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC04_MainFlow_GoiYNuocDiTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-04 Main Flow: testStep4_1_3_GetValidMovesBanDau - 4 ô hợp lệ ban đầu cho Đen chính xác.
.  [PASS] UC-04 Main Flow: testStep4_1_6_HighlightOchoHopLe - Ô hợp lệ highlight xanh nhạt, ô không hợp lệ giữ LIGHT_GRAY.
.  [PASS] UC-04 Main Flow: testStep4_1_0_CapNhatGoiYSauDatQuan - Gợi ý cập nhật chính xác sau khi chuyển lượt.

Time: 0.163

OK (3 tests)
*/

