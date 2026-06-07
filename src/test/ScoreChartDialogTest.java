package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.ScoreChartDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreChartDialogTest {

    private JFrame mockParent;
    private ScoreChartDialog dialog;

    @BeforeEach
    public void setUp() {
        mockParent = new JFrame();
        mockParent.setVisible(true);
    }

    @AfterEach
    public void tearDown() {
        if (dialog != null) {
            dialog.dispose();
        }
        mockParent.dispose();
    }

    @Test
    public void testDialogInitializationWithData() {
        // Giả lập lịch sử trận đấu kéo dài 4 lượt
        List<Integer> blackHistory = Arrays.asList(2, 4, 3, 5);
        List<Integer> whiteHistory = Arrays.asList(2, 1, 3, 2);

        dialog = new ScoreChartDialog(mockParent, blackHistory, whiteHistory);

        // Kiểm tra các thuộc tính hiển thị cơ bản của Dialog phong độ
        assertEquals("Biểu đồ Diễn biến Trận đấu (Phong độ)", dialog.getTitle(), "Tiêu đề biểu đồ phải chính xác");
        assertTrue(dialog.isModal(), "Dialog biểu đồ phải ở chế độ modal");
        assertEquals(650, dialog.getWidth(), "Chiều rộng biểu đồ phải là 650");
        assertEquals(450, dialog.getHeight(), "Chiều cao biểu đồ phải là 450");

        // Kiểm tra xem Panel vẽ đồ thị đã được nhúng vào Dialog chưa
        assertNotNull(dialog.getContentPane().getComponent(0), "Dialog phải chứa ChartPanel");
    }

    @Test
    public void testDialogWithEmptyData() {
        // Trường hợp biên: Trận đấu mới bắt đầu hoặc bị hủy ngang chưa có dữ liệu lượt đi
        List<Integer> emptyBlack = new ArrayList<>();
        List<Integer> emptyWhite = new ArrayList<>();

        // Test xem Dialog có bị crash (Lỗi NullPointerException hoặc Chia cho 0) hay không
        assertDoesNotThrow(() -> {
            dialog = new ScoreChartDialog(mockParent, emptyBlack, emptyWhite);
        }, "Hệ thống phải xử lý mượt mà khi dữ liệu lịch sử trận đấu trống rỗng");
    }
}

/*
 * Kết quả chạy
 * 673 ms: Pass
 * testDialogInitializationWithData(): 599 ms
 * testDialogWithEmptyData(): 74 ms
 * */