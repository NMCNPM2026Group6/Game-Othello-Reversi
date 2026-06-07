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
 * UC-09: CHƠI LẠI (RESET GAME) — KIỂM THỬ LUỒNG CHÍNH (Main Flow)
 * ===================================================================
 * Tham chiếu: UC-09: Chơi lại (Reset Game)
 *
 * Luồng chính mô tả quy trình reset bàn cờ về trạng thái ban đầu:
 *   9.1.0. Người dùng nhấn nút "Chơi lại" → Controller gọi model.resetGame()
 *   9.1.1. Model.resetGame() khởi tạo lại bàn cờ (4 quân trung tâm, điểm 2-2, lượt Đen)
 *   9.1.2. Controller gọi view.updateView() với board, scores, validMoves mới
 *   9.1.3. View cập nhật: ô trống, quân đen/trắng, highlight các nước đi hợp lệ cho Đen (4 ô ban đầu)
 *   9.1.4. Các ô hợp lệ được tô màu xanh nhạt RGB(200,255,200)
 *   9.1.5. Các ô không hợp lệ giữ nguyên màu nền mặc định (Color.LIGHT_GRAY)
 *
 * Các test case bao phủ:
 *   - Bước 9.1.1: resetGame() đưa model về đúng trạng thái khởi tạo
 *   - Bước 9.1.3–9.1.4: View highlight đúng 4 ô hợp lệ ban đầu cho Đen
 *   - Bước 9.1.5: Các ô khác không bị highlight
 *   - Sau reset, điểm số, lượt chơi được reset chính xác
 */
public class UC09_ResetGameTest {
    private ReversiModel model;
    private ReversiView view;

    /** Màu xanh nhạt dùng để highlight ô hợp lệ (theo UC-04) */
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
    // Bước 9.1.1: resetGame() đưa model về trạng thái khởi tạo chuẩn
    // Kiểm tra: bàn cờ 4 quân trung tâm, điểm 2-2, lượt Đen
    // =====================================================================
    @Test
    public void testStep9_1_1_ResetGameModel() {
        // Thay đổi trạng thái model (giả lập đã chơi vài nước)
        model.DatQuanCo(2, 3); // Đen đi (2,3)
        model.DatQuanCo(3, 2); // Trắng đi (3,2)
        assertNotEquals("Điểm không còn 2-2 sau khi đi", 2, model.getBlackScore());

        // Reset game
        model.resetGame();

        // Kiểm tra bàn cờ
        int[][] board = model.getBoard();
        assertEquals("(3,3) là Đen", ReversiModel.BLACK, board[3][3]);
        assertEquals("(3,4) là Trắng", ReversiModel.WHITE, board[3][4]);
        assertEquals("(4,3) là Trắng", ReversiModel.WHITE, board[4][3]);
        assertEquals("(4,4) là Đen", ReversiModel.BLACK, board[4][4]);

        // Kiểm tra điểm
        assertEquals("Điểm Đen = 2", 2, model.getBlackScore());
        assertEquals("Điểm Trắng = 2", 2, model.getWhiteScore());

        // Kiểm tra lượt
        assertEquals("Lượt là Đen", ReversiModel.BLACK, model.getLuotChoiHienTai());

        System.out.println("  [PASS] UC-09 Main Flow: testStep9_1_1_ResetGameModel - Model reset chính xác.");
    }

    // =====================================================================
    // Bước 9.1.3–9.1.4: Sau reset, view highlight đúng 4 ô hợp lệ ban đầu (2,3),(3,2),(4,5),(5,4)
    // =====================================================================
    @Test
    public void testStep9_1_3_HighlightSauReset() throws Exception {
        // Reset model và cập nhật view
        model.resetGame();
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);

        JButton[][] cells = getCells();

        // 4 ô hợp lệ phải được highlight màu xanh nhạt
        assertEquals("(2,3) highlight xanh nhạt", MAU_GOI_Y, cells[2][3].getBackground());
        assertEquals("(3,2) highlight xanh nhạt", MAU_GOI_Y, cells[3][2].getBackground());
        assertEquals("(4,5) highlight xanh nhạt", MAU_GOI_Y, cells[4][5].getBackground());
        assertEquals("(5,4) highlight xanh nhạt", MAU_GOI_Y, cells[5][4].getBackground());

        System.out.println("  [PASS] UC-09 Main Flow: testStep9_1_3_HighlightSauReset - 4 ô hợp lệ ban đầu được highlight.");
    }

    // =====================================================================
    // Bước 9.1.5: Các ô không hợp lệ giữ nguyên màu nền mặc định (LIGHT_GRAY)
    // =====================================================================
    @Test
    public void testStep9_1_5_NonValidCellsNotHighlighted() throws Exception {
        model.resetGame();
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);

        JButton[][] cells = getCells();

        // Kiểm tra một vài ô chắc chắn không hợp lệ (ví dụ góc)
        assertFalse("(0,0) không hợp lệ", validMoves[0][0]);
        assertFalse("(0,1) không hợp lệ", validMoves[0][1]);
        assertFalse("(7,7) không hợp lệ", validMoves[7][7]);

        // Các ô đó phải có màu nền mặc định Color.LIGHT_GRAY
        assertEquals("(0,0) giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[0][0].getBackground());
        assertEquals("(0,1) giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[0][1].getBackground());
        assertEquals("(7,7) giữ LIGHT_GRAY", Color.LIGHT_GRAY, cells[7][7].getBackground());

        System.out.println("  [PASS] UC-09 Main Flow: testStep9_1_5_NonValidCellsNotHighlighted - Ô không hợp lệ không bị highlight.");
    }

    // =====================================================================
    // Kiểm tra tổng hợp: Sau reset, bàn cờ hiển thị đúng quân, điểm, lượt, và gợi ý
    // =====================================================================
    @Test
    public void testResetGameComplete() throws Exception {
        // Thay đổi trạng thái
        model.DatQuanCo(2, 3);
        model.DatQuanCo(3, 2);
        assertTrue(model.getBlackScore() + model.getWhiteScore() > 4);

        // Reset
        model.resetGame();
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);

        JButton[][] cells = getCells();

        // Kiểm tra nội dung text của các ô trung tâm
        assertEquals("Ô (3,3) hiển thị quân Đen (●)", "●", cells[3][3].getText());
        assertEquals("Ô (3,4) hiển thị quân Trắng (●)", "●", cells[3][4].getText());
        assertEquals("Ô (4,3) hiển thị quân Trắng (●)", "●", cells[4][3].getText());
        assertEquals("Ô (4,4) hiển thị quân Đen (●)", "●", cells[4][4].getText());

        // Kiểm tra màu chữ (foreground) của quân
        assertEquals("Quân Đen màu đen", Color.BLACK, cells[3][3].getForeground());
        assertEquals("Quân Trắng màu trắng", Color.WHITE, cells[3][4].getForeground());

        // Kiểm tra highlight
        assertEquals("(2,3) highlight xanh nhạt", MAU_GOI_Y, cells[2][3].getBackground());
        assertEquals("(3,2) highlight xanh nhạt", MAU_GOI_Y, cells[3][2].getBackground());

        System.out.println("  [PASS] UC-09 Main Flow: testResetGameComplete - Reset hoàn chỉnh: bàn cờ, điểm, lượt, gợi ý đúng.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC09_ResetGameTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT DỰ KIẾN):
JUnit version 4.13.2
.  [PASS] UC-09 Main Flow: testStep9_1_1_ResetGameModel - Model reset chính xác.
.  [PASS] UC-09 Main Flow: testStep9_1_3_HighlightSauReset - 4 ô hợp lệ ban đầu được highlight.
.  [PASS] UC-09 Main Flow: testStep9_1_5_NonValidCellsNotHighlighted - Ô không hợp lệ không bị highlight.
.  [PASS] UC-09 Main Flow: testResetGameComplete - Reset hoàn chỉnh: bàn cờ, điểm, lượt, gợi ý đúng.

Time: 0.215

OK (4 tests)
*/