package view;

import javax.swing.*;
import java.awt.*;

public class HowToPlayDialog extends JDialog {

    public HowToPlayDialog(JFrame parent) {
        super(parent, "Hướng Dẫn Chơi Othello", true); // modal
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(getHtmlContent());
        textPane.setEditable(false);
        textPane.setCaretPosition(0); // scroll to top

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnClose = new JButton("Đã hiểu");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnClose);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Close on Escape
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
                "  <li><b>Đặt quân hợp lệ:</b> Quân mới phải kẹp được ít nhất 1 quân đối thủ " +
                "      theo hàng ngang, dọc, hoặc chéo.</li>" +
                "  <li><b>Lật quân:</b> Tất cả quân đối thủ bị kẹp giữa sẽ bị lật thành " +
                "      màu của bạn.</li>" +
                "  <li><b>Bỏ lượt:</b> Nếu không có nước đi hợp lệ, tự động bỏ lượt.</li>" +
                "  <li><b>Kết thúc:</b> Khi cả hai bên đều không thể đi, hoặc bàn cờ đầy.</li>" +
                "  <li><b>Thắng:</b> Ai có nhiều quân hơn sẽ thắng.</li>" +
                "</ol>" +
                "<h3>Ví dụ lật quân</h3>" +
                "<pre style='font-size: 14px; background: #E8F5E9; padding: 8px;'>" +
                "Trước:  ○ ● ● ● .\n" +
                "Đặt ○ vào vị trí cuối:\n" +
                "Sau:    ○ ○ ○ ○ ○\n" +
                "(Tất cả ● bị kẹp giữa 2 ○ → lật thành ○)" +
                "</pre>" +
                "<h3>Chế độ chơi</h3>" +
                "<ul>" +
                "  <li><b>2 Người:</b> Hai người chơi luân phiên trên cùng máy.</li>" +
                "  <li><b>Đấu máy - Dễ:</b> AI suy nghĩ nông, dễ thắng.</li>" +
                "  <li><b>Đấu máy - Bình thường:</b> AI cân bằng.</li>" +
                "  <li><b>Đấu máy - Khó:</b> AI suy nghĩ sâu, rất khó thắng.</li>" +
                "</ul>" +
                "<h3>Mẹo</h3>" +
                "<ul>" +
                "  <li>Chiếm 4 góc bàn cờ — quân ở góc không thể bị lật!</li>" +
                "  <li>Tránh đặt quân ở ô kề góc khi góc còn trống.</li>" +
                "  <li>Ô sáng xanh = nước đi hợp lệ của bạn.</li>" +
                "</ul>" +
                "</body>" +
                "</html>";
    }
}
