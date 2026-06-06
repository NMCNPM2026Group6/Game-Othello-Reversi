package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ScoreChartDialog extends JDialog {

    public ScoreChartDialog(JFrame owner, List<Integer> blackHistory, List<Integer> whiteHistory) {
        super(owner, "Biểu đồ Diễn biến Trận đấu (Phong độ)", true);
        setSize(650, 450);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Thêm panel tự vẽ đồ họa vào Dialog
        add(new ChartPanel(blackHistory, whiteHistory));
    }

    // Panel nội bộ đảm nhận việc tính toán tọa độ và vẽ bằng Graphics2D
    private static class ChartPanel extends JPanel {
        private final List<Integer> blackHistory;
        private final List<Integer> whiteHistory;
        private final int PADDING = 50; // Khoảng cách lề để vẽ hệ trục tọa độ

        public ChartPanel(List<Integer> blackHistory, List<Integer> whiteHistory) {
            this.blackHistory = blackHistory;
            this.whiteHistory = whiteHistory;
            setBackground(Color.WHITE); // Nền biểu đồ trắng sạch sẽ
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Bật khử răng cưa giúp nét vẽ đồ thị mịn, không bị thô
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int chartWidth = width - (2 * PADDING);
            int chartHeight = height - (2 * PADDING);

            // Vẽ hệ trục và đường lưới ngang (Mỗi vạch cách nhau 10 điểm)
            g2.setColor(new Color(220, 220, 220)); // Màu lưới xám nhạt
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            for (int i = 0; i <= 64; i += 10) {
                // Tọa độ Y đảo ngược trong Java Graphics (0 ở trên, Max ở dưới)
                int y = height - PADDING - (i * chartHeight / 64);
                g2.drawLine(PADDING, y, width - PADDING, y);

                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(i), PADDING - 25, y + 4);
                g2.setColor(new Color(220, 220, 220));
            }

            // Vẽ 2 trục chính X và Y
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(PADDING, PADDING, PADDING, height - PADDING); // Trục Y (Điểm)
            g2.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING); // Trục X (Lượt)

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Điểm", PADDING - 35, PADDING - 15);
            g2.drawString("Lượt đi", width - PADDING, height - PADDING + 20);

            // Tính toán bước nhảy trục X
            int totalTurns = blackHistory.size();
            if (totalTurns <= 1) {
                g2.drawString("Chưa có dữ liệu trận đấu để hiển thị.", width / 2 - 100, height / 2);
                return;
            }
            double xStep = (double) chartWidth / (totalTurns - 1);

            // Vẽ đường phong độ quân đen
            g2.setColor(new Color(50, 50, 50)); // Màu đen đậm nhã nhặn
            g2.setStroke(new BasicStroke(2.5f));
            drawHistoryLine(g2, blackHistory, xStep, chartHeight, height);

            // Vẽ đường phong độ quân trắng
            g2.setColor(new Color(0, 120, 215)); // Dùng màu xanh dương đại diện quân Trắng để dễ nhìn trên nền trắng
            g2.setStroke(new BasicStroke(2.5f));
            drawHistoryLine(g2, whiteHistory, xStep, chartHeight, height);

            // Vẽ chú thích (legend) ở góc trên
            g2.setStroke(new BasicStroke(1.0f));
            // Quân Đen
            g2.setColor(new Color(50, 50, 50));
            g2.fillRect(PADDING + 20, PADDING - 30, 15, 10);
            g2.drawString("Quân Đen", PADDING + 42, PADDING - 20);
            // Quân Trắng
            g2.setColor(new Color(0, 120, 215));
            g2.fillRect(PADDING + 140, PADDING - 30, 15, 10);
            g2.drawString("Quân Trắng", PADDING + 162, PADDING - 20);
        }

        // Hàm phụ duyệt mảng để nối các tọa độ điểm thành đường zigzag
        private void drawHistoryLine(Graphics2D g2, List<Integer> history, double xStep, int chartHeight, int height) {
            for (int i = 0; i < history.size() - 1; i++) {
                int x1 = PADDING + (int) (i * xStep);
                int y1 = height - PADDING - (history.get(i) * chartHeight / 64);
                int x2 = PADDING + (int) ((i + 1) * xStep);
                int y2 = height - PADDING - (history.get(i + 1) * chartHeight / 64);

                g2.drawLine(x1, y1, x2, y2); // Nối điểm cũ sang điểm mới

                // Vẽ nút tròn nhỏ tại điểm mốc để tăng tính trực quan
                g2.fillOval(x1 - 3, y1 - 3, 6, 6);
                if (i == history.size() - 2) {
                    g2.fillOval(x2 - 3, y2 - 3, 6, 6); // Điểm cuối cùng
                }
            }
        }
    }
}