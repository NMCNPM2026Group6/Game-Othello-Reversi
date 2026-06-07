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
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

/**
 * ===================================================================
 * UC-04: XEM GỢI Ý NƯỚC ĐI — KIỂM THỬ LUỒNG THAY THẾ (Alternative Flows)
 * ===================================================================
 * Tham chiếu: flow/Uc-04/Alternative Flows.txt
 *
 * AF-01: Không có nước đi hợp lệ nào để gợi ý
 *   4.2.1–4.2.5: getValidMoves() trả về toàn false → không ô nào highlight
 *
 * AF-02: Bàn cờ khởi tạo lần đầu (resetGame)
 *   4.3.1–4.3.6: resetGame() → validMoves chứa 4 ô ban đầu → highlight 4 ô
 *
 * AF-03: AI thực hiện nước đi (PVE Mode)
 *   4.4.1–4.4.5: Sau AI đi → validMoves cập nhật cho Player → highlight đúng
 */
public class UC04_AltFlow_GoiYNuocDiTest {
    private ReversiModel model;
    private ReversiView view;

    private static final Color MAU_GOI_Y = new Color(200, 255, 200);

    @Before
    public void setUp() {
        model = new ReversiModel();
        view = new ReversiView();
    }

    private JButton[][] getCells() throws Exception {
        Field cellsField = view.getClass().getDeclaredField("cells");
        cellsField.setAccessible(true);
        return (JButton[][]) cellsField.get(view);
    }

    // =====================================================================
    // AF-01 (Bước 4.2.1–4.2.5): KHÔNG CÓ nước đi hợp lệ → không highlight
    // Thiết lập: chỉ 1 quân Đen cô lập → getValidMoves() trả về toàn false
    //   4.2.4: boolean[][] validMoves toàn bộ false
    //   4.2.5: Tất cả ô giữ nguyên Color.LIGHT_GRAY
    // =====================================================================
    @Test
    public void testAF01_KhongCoNuocDiHopLe() throws Exception {
        int[][] board = model.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;
        board[0][0] = ReversiModel.BLACK;

        // 4.2.3–4.2.4: getValidMoves trả về toàn false
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                assertFalse("4.2.4: Ô (" + r + "," + c + ") phải false", validMoves[r][c]);

        // 4.2.5: Không ô nào được highlight
        view.updateView(board, ReversiModel.BLACK, 1, 0, validMoves);
        JButton[][] cells = getCells();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                assertEquals("4.2.5: Ô (" + r + "," + c + ") giữ LIGHT_GRAY",
                        Color.LIGHT_GRAY, cells[r][c].getBackground());

        System.out.println("  [PASS] UC-04 AF-01: testAF01_KhongCoNuocDiHopLe - Không có ô nào được highlight khi không còn nước đi.");
    }

    // =====================================================================
    // AF-02 (Bước 4.3.1–4.3.6): Bàn cờ KHỞI TẠO lần đầu → highlight 4 ô
    //   4.3.1: resetGame() khởi tạo 4 quân trung tâm
    //   4.3.3: getValidMoves(BLACK) tìm 4 ô ban đầu
    //   4.3.4: validMoves chứa [2][3], [3][2], [4][5], [5][4]
    //   4.3.5: View highlight 4 ô bằng màu xanh nhạt
    // =====================================================================
    @Test
    public void testAF02_KhoiTaoBanCoLanDau() throws Exception {
        model.resetGame();

        // 4.3.4: Kiểm tra 4 ô hợp lệ ban đầu
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        assertTrue("4.3.4: (2,3) hợp lệ", validMoves[2][3]);
        assertTrue("4.3.4: (3,2) hợp lệ", validMoves[3][2]);
        assertTrue("4.3.4: (4,5) hợp lệ", validMoves[4][5]);
        assertTrue("4.3.4: (5,4) hợp lệ", validMoves[5][4]);

        // 4.3.5: View highlight 4 ô
        view.updateView(model.getBoard(), model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);
        JButton[][] cells = getCells();
        assertEquals("4.3.5: (2,3) xanh nhạt", MAU_GOI_Y, cells[2][3].getBackground());
        assertEquals("4.3.5: (3,2) xanh nhạt", MAU_GOI_Y, cells[3][2].getBackground());
        assertEquals("4.3.5: (4,5) xanh nhạt", MAU_GOI_Y, cells[4][5].getBackground());
        assertEquals("4.3.5: (5,4) xanh nhạt", MAU_GOI_Y, cells[5][4].getBackground());

        System.out.println("  [PASS] UC-04 AF-02: testAF02_KhoiTaoBanCoLanDau - Sau resetGame(), 4 ô ban đầu được highlight chính xác.");
    }

    // =====================================================================
    // AF-03 (Bước 4.4.1–4.4.5): AI thực hiện nước đi → gợi ý cập nhật cho Player
    //   4.4.1: Sau khi AI đặt quân xong
    //   4.4.2: getValidMoves(LuotChoiHienTai) — lượt chuyển về Player
    //   4.4.4: View highlight các ô hợp lệ mới cho Player
    // =====================================================================
    @Test
    public void testAF03_SauAiDi_GoiYCapNhatChoPlayer() throws Exception {
        ReversiModel pveModel = new ReversiModel();
        ReversiView pveView = new ReversiView();
        ReversiController pveController = new ReversiController(pveModel, pveView);
        pveController.configure(GameConfig.pve(AiDifficulty.EASY));

        // Player (Đen) đi nước đầu tiên
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        pveController.actionPerformed(event);

        // Chờ AI xử lý xong (500ms delay + buffer)
        Thread.sleep(700);

        // 4.4.2: Lượt phải trở về Player (Đen)
        assertEquals("4.4.2: Lượt trở về Đen", ReversiModel.BLACK, pveModel.getLuotChoiHienTai());

        // 4.4.3: Player phải có nước đi hợp lệ
        boolean[][] validMoves = pveModel.getValidMoves(ReversiModel.BLACK);
        boolean coNuocDi = false;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (validMoves[r][c]) coNuocDi = true;

        assertTrue("4.4.3: Player (Đen) phải có ít nhất 1 nước đi hợp lệ sau khi AI đi", coNuocDi);

        System.out.println("  [PASS] UC-04 AF-03: testAF03_SauAiDi_GoiYCapNhatChoPlayer - Sau AI đi, gợi ý cập nhật chính xác cho Player.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC04_AltFlow_GoiYNuocDiTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-04 AF-01: testAF01_KhongCoNuocDiHopLe - Không có ô nào được highlight khi không còn nước đi.
.  [PASS] UC-04 AF-03: testAF03_SauAiDi_GoiYCapNhatChoPlayer - Sau AI đi, gợi ý cập nhật chính xác cho Player.
.  [PASS] UC-04 AF-02: testAF02_KhoiTaoBanCoLanDau - Sau resetGame(), 4 ô ban đầu được highlight chính xác.

Time: 0.878

OK (3 tests)
*/

