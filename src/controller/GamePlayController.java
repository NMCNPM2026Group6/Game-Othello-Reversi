package controller;

import model.ReversiModel;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UC-06: Xem điểm số - Hiển thị số quân Đen và Trắng trên thanh trạng thái
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

        boolean datCoThanhCong = model.DatQuanCo(row, col);

        if (datCoThanhCong) {
            // UC-06 6.1.1: Sau khi đặt quân thành công, cập nhật điểm số lên View
            updateViewFromModel();
            if (onMoveCompleted != null) {
                onMoveCompleted.run();
            }
        }
    }

    /**
     * UC-06 6.1.2: Lấy điểm số từ model (blackScore & whiteScore) và truyền cho View
     */
    public void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                // UC-06 6.1.3: model.getBlackScore() và model.getWhiteScore()
                model.getBlackScore(),
                model.getWhiteScore(),
                model.getValidMoves(model.getLuotChoiHienTai()));
    }
}
