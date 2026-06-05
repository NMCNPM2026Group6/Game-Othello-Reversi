package view;

import javax.swing.*;
import java.awt.*;

public class HowToPlayDialog extends JDialog {
    // 7.3 hàm khởi tạo HowToPlayDiaLog
    public HowToPlayDialog(JFrame parent) {
        super(parent, "Hướng Dẫn Chơi Othello", true);
        // 7.4 Cấu hình kích thước
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    // 7.5 tạo UI các components
    private void initComponents() {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        // 7.6 nhập chuỗi html vào từ hàm xây dựng sẵn
        textPane.setText(getHtmlContent());
        textPane.setEditable(false);
        textPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnClose = new JButton("Đã hiểu");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // 7.10 hệ thống xử lý dispose() giải phóng bộ nhớ
        btnClose.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // 7.9 người chơi click nút đã hiểu
        bottomPanel.add(btnClose);

        // 7.8 người chơi có thể cuộn scollpane đọc nội dung
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 7.9a người chơi nhấn phím escape trên bàn phím
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW); // 7.10a KeyStroke bắt tín hiệu và kích hoạt hàm dispose()
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
