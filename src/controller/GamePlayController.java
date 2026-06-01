package controller;

import model.ReversiModel;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UC-04: Xem gợi ý nước đi - Hệ thống tự động highlight các ô hợp lệ (màu xanh nhạt)
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
            // UC-04 4.1.1: Sau khi đặt quân thành công, cập nhật gợi ý nước đi hợp lệ
            updateViewFromModel();
            if (onMoveCompleted != null) {
                onMoveCompleted.run();
            }
        }
    }

    /**
     * UC-04 4.1.2: Truyền danh sách validMoves sang View để highlight
     */
    public void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                // UC-04 4.1.3: model.getValidMoves() trả về boolean[][] các ô hợp lệ
                model.getValidMoves(model.getLuotChoiHienTai()));
    }
}
