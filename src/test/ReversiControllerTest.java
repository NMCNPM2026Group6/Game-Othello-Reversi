package test;

import controller.ReversiController;
import model.ReversiModel;
import model.GameConfig;
import view.ReversiView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReversiControllerTest {

    private ReversiModel model;
    private ReversiView mockView;
    private ReversiController controller;

    // Lớp giả lập View ngắn gọn để kiểm tra xem Controller có tương tác đúng với View không
    private static class MockReversiView extends ReversiView {
        boolean isUpdateViewCalled = false;
        int lastLuotChoi;
        int lastBlackScore;
        int lastWhiteScore;

        @Override
        public void updateView(int[][] board, int currentPlayer, int blackScore, int whiteScore, boolean[][] validMoves) {
            this.isUpdateViewCalled = true;
            this.lastLuotChoi = currentPlayer;
            this.lastBlackScore = blackScore;
            this.lastWhiteScore = whiteScore;
        }

        @Override
        public void showMenu() {}
        @Override
        public void setModeInfo(String info) {}
    }

    @BeforeEach
    public void setUp() {
        model = new ReversiModel();
        mockView = new MockReversiView();
        controller = new ReversiController(model, mockView);
    }

    @Test
    public void testReplayTriggerFromConfigure() {
        // 1. Giả lập việc bấm nút Chơi lại thông qua hàm cấu hình cấu trúc game (configure)
        // Khi chọn chơi lại, hệ thống sẽ đưa game về cấu hình ban đầu
        controller.configure(GameConfig.pvp());

        // 2. KIỂM TRA MODEL: Đã được reset về trạng thái ban đầu chưa
        // Kiểm tra xem 4 ô trung tâm bàn cờ Reversi đã được thiết lập lại chưa
        int[][] board = model.getBoard();
        assertEquals(ReversiModel.WHITE, board[3][3], "Ô (3,3) phải là quân Trắng");
        assertEquals(ReversiModel.BLACK, board[3][4], "Ô (3,4) phải là quân Đen");
        assertEquals(ReversiModel.BLACK, board[4][3], "Ô (4,3) phải là quân Đen");
        assertEquals(ReversiModel.WHITE, board[4][4], "Ô (4,4) phải là quân Trắng");

        // Kiểm tra điểm số ban đầu
        assertEquals(2, model.getBlackScore(), "Điểm quân Đen ban đầu phải bằng 2");
        assertEquals(2, model.getWhiteScore(), "Điểm quân Trắng ban đầu phải bằng 2");

        // Kiểm tra lượt đi đầu tiên (Mặc định Reversi là quân ĐEN đi trước)
        assertEquals(ReversiModel.BLACK, model.getLuotChoiHienTai(), "Lượt đi đầu tiên sau khi chơi lại phải là quân ĐEN");

        // 3. KIỂM TRA VIEW: Controller có lệnh cho View vẽ lại toàn bộ thông số mới không
        MockReversiView testView = (MockReversiView) mockView;
        assertTrue(testView.isUpdateViewCalled, "Controller bắt buộc phải gọi hàm updateView để làm mới màn hình");
        assertEquals(ReversiModel.BLACK, testView.lastLuotChoi, "Giao diện phải hiển thị lượt đi là ĐEN");
        assertEquals(2, testView.lastBlackScore, "Giao diện điểm Đen phải reset về 2");
        assertEquals(2, testView.lastWhiteScore, "Giao diện điểm Trắng phải reset về 2");
    }
}

/*
 * Kết quả chạy
 * 527 ms: Pass
 * testReplayTriggerFromConfigure(): 527 ms
 * */