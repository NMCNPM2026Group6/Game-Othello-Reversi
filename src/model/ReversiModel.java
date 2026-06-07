package model;

public class ReversiModel {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int[][] board;
    private int LuotChoiHienTai;
    private int blackScore;
    private int whiteScore;

    private final int[] dx = { 0, 0, 1, -1, -1, 1, 1, -1 };
    private final int[] dy = { 1, -1, 0, 0, 1, 1, -1, -1 };

    public ReversiModel() {
        board = new int[8][8];
        resetGame();
    }

    public void resetGame() {
        // 9.5 vòng lặp ma trận 8x8 gán trạng thái Empty cho tất cả
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EMPTY; // 9.2b2 hệ thống giải phóng mảng cờ cũ
            }
        }
        // 9.6 đặt lại 4 quân cờ chuẩn vào trung tâm
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
        // 9.7 thiết lập lượt đi đầu tiên = BLACK
        LuotChoiHienTai = BLACK;
        // 9.8 gọi updateScore đặt điểm số về Đen: 2, Trắng: 2
        updateScore();
    }

    // UC-03: Đặt quân cờ
    public boolean DatQuanCo(int row, int col) {
        if (NuocDiHopLe(row, col, LuotChoiHienTai)) {
            board[row][col] = LuotChoiHienTai;
            latCacQuanCo(row, col);
            DoiLuot();
            updateScore();
            return true;
        }
        return false;
    }

    public void DoiLuot() {
        if (LuotChoiHienTai == BLACK) {
            LuotChoiHienTai = WHITE;
        } else {
            LuotChoiHienTai = BLACK;
        }
    }

    public boolean CoNuocDiHopLe(int player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (NuocDiHopLe(i, j, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean NuocDiHopLe(int row, int col, int player) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] != EMPTY) {
            return false;
        }

        int doiThu = (player == BLACK) ? WHITE : BLACK;

        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == doiThu) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
                return true;
            }
        }

        return false;
    }

    public boolean[][] getValidMoves(int player) {
        boolean[][] validMoves = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                validMoves[i][j] = NuocDiHopLe(i, j, player);
            }
        }
        return validMoves;
    }

    private void latCacQuanCo(int row, int col) {
        int doiThu = (LuotChoiHienTai == BLACK) ? WHITE : BLACK;

        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == doiThu) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == LuotChoiHienTai) {
                int hangLat = row + dx[i];
                int cotLat = col + dy[i];
                while (hangLat != r || cotLat != c) {
                    board[hangLat][cotLat] = LuotChoiHienTai;
                    hangLat += dx[i];
                    cotLat += dy[i];
                }
            }
        }
    }

    // UC-06 6.1.4: Đếm lại toàn bộ số lượng quân Đen và Trắng trên bàn cờ
    private void updateScore() {
        blackScore = 0;
        whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == BLACK) {
                    // UC-06 6.1.5: Tăng blackScore khi gặp quân Đen
                    blackScore++;
                } else if (board[i][j] == WHITE) {
                    // UC-06 6.1.5: Tăng whiteScore khi gặp quân Trắng
                    whiteScore++;
                }
            }
        }
    }

    public String getGameResult() {
        if (blackScore > whiteScore) {
            return "ĐEN THẮNG! (" + blackScore + " - " + whiteScore + ")";
        } else if (whiteScore > blackScore) {
            return "TRẮNG THẮNG! (" + whiteScore + " - " + blackScore + ")";
        } else {
            return "HÒA! (" + blackScore + " - " + whiteScore + ")";
        }
    }

    public int[][] getBoard() { return board; }
    public int getLuotChoiHienTai() { return LuotChoiHienTai; }
    // UC-06 6.1.6: Getter cung cấp điểm số cho Controller/View
    public int getBlackScore() { return blackScore; }
    public int getWhiteScore() { return whiteScore; }
}
