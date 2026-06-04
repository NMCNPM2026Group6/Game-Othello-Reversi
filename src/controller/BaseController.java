package controller;

import model.ReversiModel;
import view.ReversiView;

public abstract class BaseController {
    protected ReversiModel model;
    protected ReversiView view;

    protected void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                model.getValidMoves(model.getLuotChoiHienTai()));
    }
}
