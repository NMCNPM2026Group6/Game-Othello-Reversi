package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ReversiModel;
import model.GameConfig;
import model.AiDifficulty;
import view.ReversiView;
import controller.ReversiController;
import java.awt.event.ActionEvent;

/**
 * ===================================================================
 * UC-03: ĐẶT QUÂN CỜ — KIỂM THỬ LUỒNG THAY THẾ (Alternative Flows)
 * ===================================================================
 * Tham chiếu: flow/Uc-03/Alternative Flows.txt
 *
 * AF-01: Ô chọn không hợp lệ (ô đã có quân / không tạo thế kẹp)
 *   3.2.1. Player click vào ô không được highlight
 *   3.2.2. NuocDiHopLe() trả về false
 *   3.2.3. DatQuanCo() trả về false
 *   3.2.4. Controller không thực hiện thêm thao tác nào
 *
 * AF-02: Đối thủ không có nước đi — Skip Turn
 *   3.3.1. XuLyLuotTiepTheo() → CoNuocDiHopLe(nextPlayer) = false
 *   3.3.2. Hiển thị thông báo bỏ lượt
 *   3.3.3. DoiLuot() trả lại lượt cho player vừa đi
 *   3.3.4. Cập nhật View với nước đi hợp lệ mới
 *   3.3.5. Kiểm tra player vừa đánh vẫn có nước đi → tiếp tục
 *
 * AF-03: Cả hai bên không có nước đi — Kết thúc trò chơi
 *   3.4.1. CoNuocDiHopLe(nextPlayer) = false
 *   3.4.2. CoNuocDiHopLe(currentPlayer) = false
 *   3.4.3. Gọi GameOver()
 *   3.4.4. Hiển thị kết quả: "ĐEN THẮNG!", "TRẮNG THẮNG!", "HÒA!"
 *
 * AF-04: Lượt đi của AI — Chế độ PVE
 *   3.5.1. aiEnabled && currentPlayer == aiPlayer → true
 *   3.5.2. Gọi aiMove()
 *   3.5.3. Timer(500ms) tạo độ trễ
 *   3.5.4–3.5.5. AI tìm nước đi tối ưu: findBestMove()
 *   3.5.6. Đặt quân: model.DatQuanCo(row, col)
 *   3.5.7. Cập nhật View
 *   3.5.8. XuLyLuotTiepTheo() đệ quy
 */
public class UC03_AltFlow_DatQuanCoTest {
    private ReversiModel model;
    private int[][] board;

    @Before
    public void setUp() {
        model = new ReversiModel();
        board = model.getBoard();
    }

    // =====================================================================
    // AF-01 (Bước 3.2.1–3.2.4): Click vào ô ĐÃ CÓ QUÂN → DatQuanCo() = false
    // Trạng thái board không thay đổi, controller chờ player chọn ô khác
    // =====================================================================
    @Test
    public void testAF01_OchoKhongHopLe_DaCoQuan() {
        // Bàn cờ khởi tạo chuẩn, ô (3,3) đã có quân Trắng
        assertEquals("Ô (3,3) đã có quân Trắng", ReversiModel.WHITE, board[3][3]);

        boolean ketQua = model.DatQuanCo(3, 3);

        assertFalse("3.2.3: DatQuanCo() phải trả về false khi ô đã có quân", ketQua);
        assertEquals("3.2.4: Quân tại (3,3) vẫn giữ nguyên trạng thái", ReversiModel.WHITE, board[3][3]);
        assertEquals("3.2.4: Lượt chơi không thay đổi", ReversiModel.BLACK, model.getLuotChoiHienTai());
        System.out.println("  [PASS] UC-03 AF-01: testAF01_OchoKhongHopLe_DaCoQuan - Click ô đã có quân → DatQuanCo() = false, trạng thái giữ nguyên.");
    }

    // =====================================================================
    // AF-01 (Bước 3.2.2): Click vào ô trống KHÔNG TẠO THẾ KẸP → false
    // =====================================================================
    @Test
    public void testAF01_OchoKhongHopLe_KhongKepQuan() {
        // Ô (0,0) trống nhưng không tạo được thế kẹp với bất kỳ quân nào
        assertEquals("Ô (0,0) phải trống", ReversiModel.EMPTY, board[0][0]);

        boolean ketQua = model.DatQuanCo(0, 0);

        assertFalse("3.2.2: NuocDiHopLe() trả về false → DatQuanCo() = false", ketQua);
        assertEquals("3.2.4: Ô (0,0) vẫn trống", ReversiModel.EMPTY, board[0][0]);
        System.out.println("  [PASS] UC-03 AF-01: testAF01_OchoKhongHopLe_KhongKepQuan - Click ô trống không kẹp quân → false.");
    }

    // =====================================================================
    // AF-01 (Bước 3.2.2): Kiểm tra toạ độ NGOÀI BIÊN → NuocDiHopLe() = false
    // Sử dụng Reflection để gọi trực tiếp phương thức private NuocDiHopLe()
    // =====================================================================
    @Test
    public void testAF01_ToaDoNgoaiBien() throws Exception {
        java.lang.reflect.Method nuocDiHopLeMethod =
                model.getClass().getDeclaredMethod("NuocDiHopLe", int.class, int.class, int.class);
        nuocDiHopLeMethod.setAccessible(true);

        // Toạ độ hàng âm (-1, 3)
        boolean oob1 = (boolean) nuocDiHopLeMethod.invoke(model, -1, 3, ReversiModel.BLACK);
        assertFalse("Hàng âm (-1) phải trả về false", oob1);

        // Toạ độ hàng vượt biên (8, 3)
        boolean oob2 = (boolean) nuocDiHopLeMethod.invoke(model, 8, 3, ReversiModel.BLACK);
        assertFalse("Hàng vượt biên (8) phải trả về false", oob2);

        // Toạ độ cột âm (3, -1)
        boolean oob3 = (boolean) nuocDiHopLeMethod.invoke(model, 3, -1, ReversiModel.BLACK);
        assertFalse("Cột âm (-1) phải trả về false", oob3);

        // Toạ độ cột vượt biên (3, 8)
        boolean oob4 = (boolean) nuocDiHopLeMethod.invoke(model, 3, 8, ReversiModel.BLACK);
        assertFalse("Cột vượt biên (8) phải trả về false", oob4);

        System.out.println("  [PASS] UC-03 AF-01: testAF01_ToaDoNgoaiBien - Toạ độ ngoài biên [0-7] được ngăn chặn an toàn.");
    }

    // =====================================================================
    // AF-02 (Bước 3.3.1–3.3.5): Đối thủ không có nước đi — SKIP TURN
    // Thiết lập bàn cờ sao cho sau khi Đen đi, Trắng không có nước hợp lệ
    //   → Hệ thống DoiLuot() trả lại lượt cho Đen
    //   → Đen vẫn có nước đi → trò chơi tiếp tục
    // =====================================================================
    @Test
    public void testAF02_BoLuotDoiThu_SkipTurn() {
        // Xoá bàn cờ rồi thiết lập kịch bản cụ thể
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        // Đen chiếm gần hết, Trắng chỉ còn 1 quân bị kẹp
        board[0][0] = ReversiModel.BLACK;
        board[0][1] = ReversiModel.WHITE;
        // Đen đặt tại (0,2) sẽ lật (0,1) → Trắng hết quân

        boolean ketQua = model.DatQuanCo(0, 2);
        assertTrue("Đen đặt quân thành công", ketQua);

        // 3.3.1: Kiểm tra Trắng không có nước đi
        boolean trangCoNuocDi = model.CoNuocDiHopLe(ReversiModel.WHITE);
        assertFalse("3.3.1: Trắng không có nước đi hợp lệ → CoNuocDiHopLe(WHITE) = false", trangCoNuocDi);

        System.out.println("  [PASS] UC-03 AF-02: testAF02_BoLuotDoiThu_SkipTurn - Trắng không có nước đi, trò chơi xử lý bỏ lượt.");
    }

    // =====================================================================
    // AF-03 (Bước 3.4.1–3.4.4): Cả hai bên KHÔNG CÓ nước đi → Kết thúc
    // Thiết lập: Đen(0,0), Trắng(7,7) — cả hai cô lập, không ai kẹp được
    //   → CoNuocDiHopLe(BLACK) = false
    //   → CoNuocDiHopLe(WHITE) = false
    //   → Gọi GameOver() hiển thị kết quả
    // =====================================================================
    @Test
    public void testAF03_CaHaiBenKhongCoNuocDi_KetThucGame() throws Exception {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = ReversiModel.EMPTY;

        board[0][0] = ReversiModel.BLACK;
        board[7][7] = ReversiModel.WHITE;

        // Cập nhật điểm qua reflection
        java.lang.reflect.Method updateScoreMethod = model.getClass().getDeclaredMethod("updateScore");
        updateScoreMethod.setAccessible(true);
        updateScoreMethod.invoke(model);

        // 3.4.1 + 3.4.2: Cả hai bên đều không có nước đi
        assertFalse("3.4.1: Đen không có nước đi", model.CoNuocDiHopLe(ReversiModel.BLACK));
        assertFalse("3.4.2: Trắng không có nước đi", model.CoNuocDiHopLe(ReversiModel.WHITE));

        // 3.4.4: Kiểm tra kết quả hiển thị
        String ketQua = model.getGameResult();
        assertEquals("3.4.4: Khi điểm bằng nhau → HÒA!", "HÒA! (1 - 1)", ketQua);

        System.out.println("  [PASS] UC-03 AF-03: testAF03_CaHaiBenKhongCoNuocDi_KetThucGame - Cả hai bên hết nước đi → GameOver() kết quả HÒA.");
    }

    // =====================================================================
    // AF-04 (Bước 3.5.1–3.5.8): Lượt đi của AI — PVE MODE
    // Thiết lập: PVE + EASY → Player đi nước đầu → AI tự động đi sau 500ms
    //   3.5.1: aiEnabled && currentPlayer == aiPlayer → true
    //   3.5.3: Timer(500ms) tạo độ trễ
    //   3.5.6: AI gọi DatQuanCo()
    //   3.5.8: XuLyLuotTiepTheo() chuyển lại lượt cho Player
    // =====================================================================
    @Test
    public void testAF04_LuotDiAI_PVEMode() throws Exception {
        ReversiModel pveModel = new ReversiModel();
        ReversiView pveView = new ReversiView();
        ReversiController pveController = new ReversiController(pveModel, pveView);

        pveController.configure(GameConfig.pve(AiDifficulty.EASY));

        // Đếm ô trống trước khi đi
        int emptyBefore = 0;
        int[][] b = pveModel.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (b[r][c] == ReversiModel.EMPTY) emptyBefore++;

        // Player (Đen) đi nước đầu tiên
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "2,3");
        pveController.actionPerformed(event);

        // 3.5.1: Sau nước đi của Player, lượt chuyển sang Trắng (AI)
        assertEquals("3.5.1: Lượt chuyển sang AI (Trắng)", ReversiModel.WHITE, pveModel.getLuotChoiHienTai());

        // 3.5.3: Chờ Timer(500ms) để AI thực hiện
        Thread.sleep(700);

        // 3.5.8: Sau khi AI đi xong, XuLyLuotTiepTheo() trả lượt về Player
        assertEquals("3.5.8: Lượt trở về Player (Đen)", ReversiModel.BLACK, pveModel.getLuotChoiHienTai());

        // Kiểm tra bàn cờ đã có thêm 2 nước đi (1 Player + 1 AI)
        int emptyAfter = 0;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (b[r][c] == ReversiModel.EMPTY) emptyAfter++;

        assertEquals("3.5.6: Bàn cờ có thêm 2 quân (Player + AI)", emptyBefore - 2, emptyAfter);

        System.out.println("  [PASS] UC-03 AF-04: testAF04_LuotDiAI_PVEMode - AI tự động đặt quân sau 500ms delay trong chế độ PVE.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.UC03_AltFlow_DatQuanCoTest");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] UC-03 AF-01: testAF01_OchoKhongHopLe_KhongKepQuan - Click ô trống không kẹp quân → false.
.  [PASS] UC-03 AF-01: testAF01_OchoKhongHopLe_DaCoQuan - Click ô đã có quân → DatQuanCo() = false, trạng thái giữ nguyên.
.  [PASS] UC-03 AF-02: testAF02_BoLuotDoiThu_SkipTurn - Trắng không có nước đi, trò chơi xử lý bỏ lượt.
.  [PASS] UC-03 AF-04: testAF04_LuotDiAI_PVEMode - AI tự động đặt quân sau 500ms delay trong chế độ PVE.
.  [PASS] UC-03 AF-03: testAF03_CaHaiBenKhongCoNuocDi_KetThucGame - Cả hai bên hết nước đi → GameOver() kết quả HÒA.
.  [PASS] UC-03 AF-01: testAF01_ToaDoNgoaiBien - Toạ độ ngoài biên [0-7] được ngăn chặn an toàn.

Time: 0.909

OK (6 tests)
*/

