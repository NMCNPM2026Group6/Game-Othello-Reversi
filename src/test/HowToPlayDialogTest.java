package test ;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.HowToPlayDialog;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import static org.junit.jupiter.api.Assertions.*;

public class HowToPlayDialogTest {

    private JFrame mockParent;
    private HowToPlayDialog dialog;

    @BeforeEach
    public void setUp() {
        // Tạo một Frame giả lập đóng vai trò Menu chính của game Reversi
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
    public void testDialogInitialization() {
        // Khởi tạo dialog (chưa hiển thị vì chưa gọi setVisible)
        dialog = new HowToPlayDialog(mockParent);

        // Kiểm tra các thuộc tính xem có đúng yêu cầu thiết kế không
        assertEquals("Hướng Dẫn Chơi Othello", dialog.getTitle(), "Tiêu đề dialog phải chính xác");
        assertTrue(dialog.isModal(), "Dialog phải ở chế độ modal (khóa màn hình chính)");
        assertFalse(dialog.isResizable(), "Dialog không được phép co giãn kích thước");
        assertEquals(600, dialog.getWidth(), "Chiều rộng phải là 600");
        assertEquals(650, dialog.getHeight(), "Chiều cao phải là 650");
    }

    @Test
    public void testHtmlContentNotEmpty() throws BadLocationException {
        dialog = new HowToPlayDialog(mockParent);

        // Tìm JTextPane trong Dialog để xem nó có chứa nội dung luật chơi không
        JScrollPane scrollPane = (JScrollPane) dialog.getContentPane().getComponent(0);
        JTextPane textPane = (JTextPane) scrollPane.getViewport().getView();

        String plainText = textPane.getDocument().getText(0, textPane.getDocument().getLength());
        assertNotNull(plainText, "Nội dung hướng dẫn không được để trống");
        assertTrue(plainText.contains("OTHELLO/REVERSI"), "Nội dung phải chứa tiêu đề game");
        assertTrue(plainText.contains("Mục tiêu"), "Nội dung phải có phần Mục tiêu");
        assertTrue(plainText.contains("Chiếm 4 góc bàn cờ"), "Nội dung phải có phần Mẹo");
    }
}

/*
* Kết quả chạy
* 1 sec 99 ms: Pass
* testDialogInitialization(): 967 ms
* testHtmlContentNotEmpty(): 132 ms
* */