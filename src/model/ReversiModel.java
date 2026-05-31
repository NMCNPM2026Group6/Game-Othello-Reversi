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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EMPTY;
            }
        }

        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
        LuotChoiHienTai = BLACK;
        updateScore();
    }

    // Hàm đặt quân cờ chính (UC-03)
    public boolean DatQuanCo(int row, int col) {
        // UC-03 3.1.4 / UC-03 Alternative Flow 3.2.2
        // Kiểm tra tính hợp lệ của nước đi dựa trên tọa độ chọn và phe hiện tại
        if (NuocDiHopLe(row, col, LuotChoiHienTai)) {
            
            // UC-03 3.1.5
            // Thực hiện đặt quân cờ của người chơi hiện tại vào vị trí đã chọn
            board[row][col] = LuotChoiHienTai;
            
            // UC-03 3.1.6
            // Lật các quân cờ của đối thủ nằm giữa hai quân cờ của người chơi hiện tại
            latCacQuanCo(row, col);
            
            // UC-03 3.1.7
            // Thực hiện đổi lượt chơi (chuyển đổi BLACK ↔ WHITE)
            DoiLuot();
            
            // UC-03 3.1.8
            // Tính toán và đếm lại số lượng quân cờ của mỗi bên để cập nhật điểm số
            updateScore();
            
            return true;
        }
        
        // UC-03 Alternative Flow 3.2.3
        // Trả về kết quả false báo hiệu nước đi không thể thực hiện
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

    // Logic kiểm tra quy tắc (chỉ kiểm tra, không lật)
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

    private void updateScore() {
        blackScore = 0;
        whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == BLACK) {
                    blackScore++;
                } else if (board[i][j] == WHITE) {
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
    public int getBlackScore() { return blackScore; }
    public int getWhiteScore() { return whiteScore; }
}
