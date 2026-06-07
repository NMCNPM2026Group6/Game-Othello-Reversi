package view;

import javax.swing.*;
import java.awt.*;

public class HowToPlayDialog extends JDialog {

    // UC-07 7.1.5: Khởi tạo Dialog hướng dẫn chơi (modal)
    public HowToPlayDialog(JFrame parent) {
        super(parent, "Hướng Dẫn Chơi Othello", true);
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    // UC-07 7.1.6: Dựng giao diện nội dung hướng dẫn
    private void initComponents() {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(getHtmlContent());
        textPane.setEditable(false);
        textPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // UC-07 7.1.7: Nút "Đã hiểu" -> dispose() đóng dialog
        JButton btnClose = new JButton("Đã hiểu");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnClose);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private String getHtmlContent() {
        return "<html>" +
                "<body style='font-family: Segoe UI, Arial; font-size: 13px; padding: 10px;'>" +
                "<h2 style='text-align: center; color: #2E7D32;'>OTHELLO/REVERSI</h2>" +
                "<hr/>" +
                "<h3>Mục tiêu</h3>" +
                "<p>Chiếm nhiều quân cờ nhất trên bàn cờ 8×8 khi trò chơi kết thúc.</p>" +
                "<h3>Luật chơi</h3>" +
                "<ol>" +
                "  <li><b>Hai bên:</b> Đen (đi trước) và Trắng luân phiên đặt quân.</li>" +
                "  <li><b>Đặt quân hợp lệ:</b> Quân mới phải kẹp được ít nhất 1 quân đối thủ theo hàng ngang, dọc, hoặc chéo.</li>" +
                "  <li><b>Lật quân:</b> Tất cả quân đối thủ bị kẹp giữa sẽ bị lật thành màu của bạn.</li>" +
                "  <li><b>Bỏ lượt:</b> Nếu không có nước đi hợp lệ, tự động bỏ lượt.</li>" +
                "  <li><b>Kết thúc:</b> Khi cả hai bên đều không thể đi, hoặc bàn cờ đầy.</li>" +
                "</ol>" +
                "</body>" +
                "</html>";
    }
}
