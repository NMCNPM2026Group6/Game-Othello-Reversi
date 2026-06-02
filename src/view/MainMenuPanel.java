package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {
    private JButton btnPvp;
    private JButton btnPve;
    private JButton btnAiEasy;
    private JButton btnAiNormal;
    private JButton btnAiHard;
    private JButton btnHowToPlay;
    private JButton btnExit;
    private JButton btnBackToMain;

    private CardLayout menuCardLayout;
    private JPanel menuCards;

    public MainMenuPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("OTHELLO / REVERSI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(title);
        container.add(Box.createRigidArea(new Dimension(0, 40)));

        // CardLayout để chuyển giữa Menu chính và Chọn độ khó
        menuCardLayout = new CardLayout();
        menuCards = new JPanel(menuCardLayout);

        JPanel mainCard = new JPanel();
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));

        btnPvp = createMenuButton("Chơi 2 Người", Color.decode("#2196F3"));
        btnPve = createMenuButton("Đấu với máy", Color.decode("#4CAF50"));
        btnHowToPlay = createMenuButton("Hướng dẫn", Color.decode("#9C27B0"));
        btnExit = createMenuButton("Thoát", Color.decode("#616161"));

        mainCard.add(btnPvp);
        mainCard.add(Box.createRigidArea(new Dimension(0, 10)));
        mainCard.add(btnPve);
        mainCard.add(Box.createRigidArea(new Dimension(0, 10)));
        mainCard.add(btnHowToPlay);
        mainCard.add(Box.createRigidArea(new Dimension(0, 10)));
        mainCard.add(btnExit);

        JPanel aiCard = new JPanel();
        aiCard.setLayout(new BoxLayout(aiCard, BoxLayout.Y_AXIS));

        JLabel aiLabel = new JLabel("CHỌN ĐỘ KHÓ");
        aiLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        aiLabel.setForeground(new Color(50, 50, 50));
        aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnAiEasy = createMenuButton("Dễ", Color.decode("#4CAF50"));
        btnAiNormal = createMenuButton("Bình thường", Color.decode("#FF9800"));
        btnAiHard = createMenuButton("Khó", Color.decode("#F44336"));
        btnBackToMain = createMenuButton("Quay lại", Color.decode("#757575"));

        aiCard.add(aiLabel);
        aiCard.add(Box.createRigidArea(new Dimension(0, 20)));
        aiCard.add(btnAiEasy);
        aiCard.add(Box.createRigidArea(new Dimension(0, 10)));
        aiCard.add(btnAiNormal);
        aiCard.add(Box.createRigidArea(new Dimension(0, 10)));
        aiCard.add(btnAiHard);
        aiCard.add(Box.createRigidArea(new Dimension(0, 20)));
        aiCard.add(btnBackToMain);

        menuCards.add(mainCard, "MAIN");
        menuCards.add(aiCard, "AI");

        container.add(menuCards);
        add(container);

        // Logic chuyển đổi nội bộ
        btnPve.addActionListener(e -> menuCardLayout.show(menuCards, "AI"));
        btnBackToMain.addActionListener(e -> menuCardLayout.show(menuCards, "MAIN"));
    }

    private JButton createMenuButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(250, 45));
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            Color original = btn.getBackground();

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(original.brighter());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });

        return btn;
    }

    public void resetToMain() {
        menuCardLayout.show(menuCards, "MAIN");
    }

    // Listener registration methods
    public void addPvpListener(ActionListener l) {
        btnPvp.addActionListener(l);
    }

    public void addAiEasyListener(ActionListener l) {
        btnAiEasy.addActionListener(l);
    }

    public void addAiNormalListener(ActionListener l) {
        btnAiNormal.addActionListener(l);
    }

    public void addAiHardListener(ActionListener l) {
        btnAiHard.addActionListener(l);
    }

    public void addHowToPlayListener(ActionListener l) {
        btnHowToPlay.addActionListener(l);
    }

    public void addExitListener(ActionListener l) {
        btnExit.addActionListener(l);
    }
}
