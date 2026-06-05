package controller;

import model.ReversiModel;
import view.ReversiView;

public abstract class BaseController {
    protected ReversiModel model;
    protected ReversiView view;
    // UC-06 6.1.2: Lấy điểm số từ Model và truyền cho View hiển thị
    protected void updateViewFromModel() {
        // 9.11 cập nhật ui
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                // UC-06 6.1.3: model.getBlackScore() và model.getWhiteScore() trả về số quân mỗi bên
                model.getBlackScore(),
                model.getWhiteScore(),
                // 9.10 gọi hàm tính nước đi hợp lệ cho lượt chơi hiện tại
                model.getValidMoves(model.getLuotChoiHienTai()));
    }
}
