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
import javax.swing.JButton;
import java.awt.event.ActionEvent;

/**
 * ===================================================================
 * UC-09: CHƠI LẠI (RESET GAME) — KIỂM THỬ LUỒNG CHÍNH VÀ THAY THẾ
 * ===================================================================
 * Tham chiếu: UC-09: Chơi lại
 * 
 * AF-01: Reset game khi đang chơi dở (đã có nhiều nước đi)
 *   → Bàn cờ về trạng thái ban đầu, lượt về Đen, điểm 2-2, gợi ý 4 ô
 * 
 * AF-02: Reset game trong chế độ PVE (AI)
 *   → AI không tự động đi sau reset, lượt vẫn là Đen
 * 
 * AF-03: Reset game khi ván đấu đã kết thúc (hết nước đi)
 *   → Reset thành công, bàn cờ mới, điểm số reset
 */
public class UC09_ResetGameTest {
    private ReversiModel model;
    private ReversiView view;
    private ReversiController controller;

    private static final Color MAU_GOI_Y = new Color(200, 255, 200);
    private static final Color MAU_NEN_MAC_DINH = Color.LIGHT_GRAY;

    @Before
    public void setUp() {
        model = new ReversiModel();
        view = new ReversiView();
        controller = new ReversiController(model, view);
        // Không cấu hình chế độ, mặc định PVP
    }

    private JButton[][] getCells() throws Exception {
        Field cellsField = view.getClass().getDeclaredField("cells");
        cellsField.setAccessible(true);
        return (JButton[][]) cellsField.get(view);
    }

    // Mô phỏng vài nước đi để đưa game vào trạng thái "đang chơi dở"
    private void playSomeMoves() throws Exception {
        // Nước đi hợp lệ đầu tiên: Đen tại (2,3)
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        controller.actionPerformed(event);
        // Lúc này lượt là Trắng. Trắng đi tại (4,2)
        event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "4,2");
        controller.actionPerformed(event);
        // Đen đi tại (5,3)
        event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "5,3");
        controller.actionPerformed(event);
        // Trắng đi tại (2,2)
        event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,2");
        controller.actionPerformed(event);
        // Bàn cờ đã có hơn 4 quân, điểm số khác 2-2
    }

    // =====================================================================
    // AF-01: Reset game khi đang chơi dở
    //   1. Thực hiện vài nước đi
    //   2. Gọi resetGame() (hoặc click nút)
    //   3. Kiểm tra bàn cờ về khởi tạo, lượt Đen, điểm 2-2, gợi ý 4 ô
    // =====================================================================
    @Test
    public void testAF01_ResetKhiDangChoiDo() throws Exception {
        // 1. Chơi vài nước
        playSomeMoves();
        assertTrue("Số quân đen phải > 2", model.getBlackScore() > 2);
        assertTrue("Số quân trắng phải > 2", model.getWhiteScore() > 2);
        assertNotEquals("Điểm không còn là 2-2", 2, model.getBlackScore());

        // 2. Reset game (gọi trực tiếp controller hoặc mô phỏng click nút)
        // Cách 1: Gọi phương thức xử lý reset từ controller (nếu có)
        // Giả sử controller có phương thức resetGame()
        controller.resetGame();  // Bạn cần thêm phương thức này trong ReversiController
        
        // Cách 2: Mô phỏng click nút (nếu đã đăng ký listener)
        // Field btnField = view.getClass().getDeclaredField("btnResetGame");
        // btnField.setAccessible(true);
        // JButton btnReset = (JButton) btnField.get(view);
        // ActionListener[] listeners = btnReset.getActionListeners();
        // if (listeners.length > 0) listeners[0].actionPerformed(new ActionEvent(btnReset, ActionEvent.ACTION_PERFORMED, "reset"));

        // 3. Kiểm tra trạng thái sau reset
        // 3.1 Bàn cờ: 4 quân khởi tạo
        int[][] board = model.getBoard();
        assertEquals("Ô (3,3) phải là Trắng", ReversiModel.WHITE, board[3][3]);
        assertEquals("Ô (3,4) phải là Đen", ReversiModel.BLACK, board[3][4]);
        assertEquals("Ô (4,3) phải là Đen", ReversiModel.BLACK, board[4][3]);
        assertEquals("Ô (4,4) phải là Trắng", ReversiModel.WHITE, board[4][4]);
        
        // 3.2 Điểm số
        assertEquals("Điểm Đen = 2", 2, model.getBlackScore());
        assertEquals("Điểm Trắng = 2", 2, model.getWhiteScore());
        
        // 3.3 Lượt về Đen
        assertEquals("Lượt là Đen", ReversiModel.BLACK, model.getLuotChoiHienTai());
        
        // 3.4 Gợi ý: 4 ô (2,3), (3,2), (4,5), (5,4) được highlight
        boolean[][] validMoves = model.getValidMoves(ReversiModel.BLACK);
        assertTrue("(2,3) hợp lệ", validMoves[2][3]);
        assertTrue("(3,2) hợp lệ", validMoves[3][2]);
        assertTrue("(4,5) hợp lệ", validMoves[4][5]);
        assertTrue("(5,4) hợp lệ", validMoves[5][4]);
        
        // Kiểm tra view đã cập nhật gợi ý màu xanh
        view.updateView(board, model.getLuotChoiHienTai(),
                model.getBlackScore(), model.getWhiteScore(), validMoves);
        JButton[][] cells = getCells();
        assertEquals("(2,3) được highlight", MAU_GOI_Y, cells[2][3].getBackground());
        assertEquals("(3,2) được highlight", MAU_GOI_Y, cells[3][2].getBackground());
        assertEquals("(4,5) được highlight", MAU_GOI_Y, cells[4][5].getBackground());
        assertEquals("(5,4) được highlight", MAU_GOI_Y, cells[5][4].getBackground());
        
        System.out.println("  [PASS] UC-09 AF-01: Reset khi đang chơi dở → bàn cờ, điểm, lượt, gợi ý đúng.");
    }

    // =====================================================================
    // AF-02: Reset game trong chế độ PVE (AI EASY)
    //   1. Cấu hình PVE với AI Easy
    //   2. Để AI đi một nước (hoặc tự đi)
    //   3. Reset game
    //   4. Kiểm tra AI không tự động đi sau reset, lượt là Đen (Player)
    // =====================================================================
    @Test
    public void testAF02_ResetTrongCheDoPVE() throws Exception {
        // 1. Cấu hình PVE
        controller.configure(GameConfig.pve(AiDifficulty.EASY));
        
        // 2. Để AI đi (bằng cách kích hoạt AI move, hoặc để lượt AI tự động)
        // Ở đây ta chủ động gọi AI move sau khi reset? Không, ta sẽ reset khi đang trong game.
        // Giả sử sau configure, lượt là Đen (Player). Player đi một nước để chuyển sang AI.
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        controller.actionPerformed(event);
        // Chờ AI xử lý (nếu cần)
        Thread.sleep(500);
        // Bây giờ bàn cờ đã có AI đi, điểm số thay đổi
        int blackScoreBefore = model.getBlackScore();
        int whiteScoreBefore = model.getWhiteScore();
        assertTrue("AI đã đi, điểm khác 2-2", blackScoreBefore + whiteScoreBefore > 4);
        
        // 3. Reset game
        controller.resetGame();
        
        // 4. Kiểm tra sau reset
        assertEquals("Điểm Đen = 2", 2, model.getBlackScore());
        assertEquals("Điểm Trắng = 2", 2, model.getWhiteScore());
        assertEquals("Lượt là Đen (Player)", ReversiModel.BLACK, model.getLuotChoiHienTai());
        
        // Đảm bảo AI không tự động gọi sau reset (kiểm tra bằng cách chờ 1 giây rồi xem có nước đi nào mới không)
        Thread.sleep(1000);
        assertEquals("Điểm không thay đổi sau 1 giây", 2, model.getBlackScore());
        
        System.out.println("  [PASS] UC-09 AF-02: Reset trong PVE → AI không tự động đi, lượt Đen đúng.");
    }

    // =====================================================================
    // AF-03: Reset game khi ván đấu đã kết thúc (không còn nước đi)
    //   1. Tạo trạng thái kết thúc (hoặc giả lập bằng cách đi đến hết nước)
    //   2. Reset game
    //   3. Kiểm tra bàn cờ mới, không còn thông báo kết thúc
    // =====================================================================
    @Test
    public void testAF03_ResetKhiGameKetThuc() throws Exception {
        // 1. Tạo trạng thái kết thúc: có thể điền đầy bàn cờ nhanh (dùng reflection)
        // Hoặc đơn giản hơn: giả lập game over bằng cách set trực tiếp lượt hiện tại là null?
        // Cách an toàn: đi nước đi đến khi hết nước (nhưng test sẽ dài). Ta dùng reflection để ép board gần kín.
        int[][] board = model.getBoard();
        // Điền gần kín bàn cờ, chỉ để trống 1 ô không hợp lệ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i==3 && j==3) || (i==3 && j==4) || (i==4 && j==3) || (i==4 && j==4)) continue;
                board[i][j] = (i+j) % 2 == 0 ? ReversiModel.BLACK : ReversiModel.WHITE;
            }
        }
        // Đặt 4 ô trung tâm đúng
        board[3][3] = ReversiModel.BLACK;
        board[3][4] = ReversiModel.WHITE;
        board[4][3] = ReversiModel.WHITE;
        board[4][4] = ReversiModel.BLACK;
        // Gọi updateScore() qua reflection
        java.lang.reflect.Method m = model.getClass().getDeclaredMethod("updateScore");
        m.setAccessible(true);
        m.invoke(model);
        // Kiểm tra không còn nước đi
        assertFalse("Không còn nước đi cho Đen", model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse("Không còn nước đi cho Trắng", model.CoNuocDiHopLe(ReversiModel.WHITE));
        
        // 2. Reset game
        controller.resetGame();
        
        // 3. Kiểm tra bàn cờ về khởi tạo
        board = model.getBoard();
        assertEquals("(3,3) Trắng", ReversiModel.WHITE, board[3][3]);
        assertEquals("(3,4) Đen", ReversiModel.BLACK, board[3][4]);
        assertEquals("(4,3) Đen", ReversiModel.BLACK, board[4][3]);
        assertEquals("(4,4) Trắng", ReversiModel.WHITE, board[4][4]);
        assertEquals("Điểm Đen = 2", 2, model.getBlackScore());
        assertEquals("Lượt là Đen", ReversiModel.BLACK, model.getLuotChoiHienTai());
        
        System.out.println("  [PASS] UC-09 AF-03: Reset khi game kết thúc → bàn cờ khởi tạo thành công.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC09_ResetGameTest");
    }
}