package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.ReversiModel;

public class ReversiView extends JFrame {
    private JButton[][] cells;
    private JLabel statusLabel;
    private JLabel modeLabel;

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private MainMenuPanel menuPanel;
    private JPanel gamePanel;
    private JButton btnBackToMenu;

    public ReversiView() {
        // tao cua so
        setTitle("Game Reversi");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        // menu card
        menuPanel = new MainMenuPanel();
        cardContainer.add(menuPanel, "MENU");

        // game card
        gamePanel = new JPanel(new BorderLayout());

        modeLabel = new JLabel("Chế độ: ");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        gamePanel.add(modeLabel, BorderLayout.NORTH);

        // tao bang 8x8
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        cells = new JButton[8][8];

        // tao cac o
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new JButton();
                cells[i][j].setBackground(Color.LIGHT_GRAY);
                cells[i][j].setFont(new Font("Arial", Font.BOLD, 40));

                // luu vi tri nut
                cells[i][j].setActionCommand(i + "," + j);

                boardPanel.add(cells[i][j]);
            }
        }
        gamePanel.add(boardPanel, BorderLayout.CENTER);

        // hien thi trang thai va nut ve menu
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Lượt: Đen");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        btnBackToMenu = new JButton("← Về Menu");
        btnBackToMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bottomPanel.add(btnBackToMenu, BorderLayout.EAST);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        cardContainer.add(gamePanel, "GAME");

        add(cardContainer);
        setLocationRelativeTo(null);
    }

    // Navigation methods
    public void showMenu() {
        cardLayout.show(cardContainer, "MENU");
    }

    public void showGame() {
        cardLayout.show(cardContainer, "GAME");
    }

    public MainMenuPanel getMenuPanel() {
        return menuPanel;
    }

    public void addBackToMenuListener(ActionListener listener) {
        btnBackToMenu.addActionListener(listener);
    }

    // them su kien click chuot
    public void addGameListener(ActionListener listener) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j].addActionListener(listener);
            }
        }
    }

    // cap nhat ban co
    public void updateView(int[][] board, int currentPlayer, int blackScore, int whiteScore, boolean[][] validMoves) {
        // duyet qua tung o
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Reset background color
                cells[i][j].setBackground(Color.LIGHT_GRAY);

                if (board[i][j] == 1) { // quan den
                    cells[i][j].setText("O");
                    cells[i][j].setForeground(Color.BLACK);
                } else if (board[i][j] == 2) { // quan trang
                    cells[i][j].setText("O");
                    cells[i][j].setForeground(Color.WHITE);
                } else {
                    cells[i][j].setText(""); // o trong

                    // Hien thi goi y
                    if (validMoves != null && validMoves[i][j]) {
                        cells[i][j].setBackground(new Color(200, 255, 200)); // Mau xanh nhat
                    }
                }
            }
        }

        // UC-06 6.1.7: Hiển thị điểm số lên statusLabel
        // statusLabel.setText("Lượt: [Phe] | Đen: blackScore - Trắng: whiteScore")
        if (currentPlayer == 1) {
            statusLabel.setText("Lượt: Đen | Đen: " + blackScore + " - Trắng: " + whiteScore);
        } else {
            statusLabel.setText("Lượt: Trắng | Đen: " + blackScore + " - Trắng: " + whiteScore);
        }
    }

    // hien thi thong bao
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void setModeInfo(String info) {
        modeLabel.setText(info);
    }
}