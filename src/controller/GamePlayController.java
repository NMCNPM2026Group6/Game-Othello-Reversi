package controller;

import model.ReversiModel;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UC-03: Đặt quân cờ - Xử lý click vào ô hợp lệ để đặt quân
 * 
 * @author Tài
 */
public class GamePlayController implements ActionListener {
    private ReversiModel model;
    private ReversiView view;
    private Runnable onMoveCompleted;

    public GamePlayController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;
        this.view.addGameListener(this);
    }

    public void setOnMoveCompleted(Runnable callback) {
        this.onMoveCompleted = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] coords = command.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);

        // UC-03: Đặt quân cờ
        boolean datCoThanhCong = model.DatQuanCo(row, col);

        if (datCoThanhCong) {
            updateViewFromModel();
            if (onMoveCompleted != null) {
                onMoveCompleted.run();
            }
        }
    }

    public void updateViewFromModel() {
        // Cập nhật giao diện bàn cờ cơ bản
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                null); // Chưa truyền gợi ý ở UC-03
    }
}
