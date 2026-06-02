package model;

public class ReversiModel {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int[][] board;
    private int LuotChoiHienTai;
    private int blackScore;
    private int whiteScore;

    // 8 huong di chuyenể
    // |TâyBắc| |Bắc| |ĐôngBắc|
    // |Tây | | x | |Đông |
    // |TâyNam| |Nam| |ĐôngNam|

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

        // mặc định dat 4 quan o giua
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
        LuotChoiHienTai = BLACK;
        updateScore();
    }

    // dđặt quân cờ
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

    // Đổi Lượt
    public void DoiLuot() {
        if (LuotChoiHienTai == BLACK) {
            LuotChoiHienTai = WHITE;
        } else {
            LuotChoiHienTai = BLACK;
        }
    }

    // kiem tra nguoi choi co the di khong
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

    // kiem tra nước đi hợplệ (chi kiem tra, khong lat)
    private boolean NuocDiHopLe(int row, int col, int player) {
        // check o phai trong
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] != EMPTY) {
            return false;
        }

        int doiThu = (player == BLACK) ? WHITE : BLACK;

        // duyet 8 huong
        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            // di theo huong nay
            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == doiThu) {
                r += dx[i];
                c += dy[i];
                count++; // đếm đối thủ bị kẹt ở giữa
            }

            // co the lat duoc o huong nay
            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
                return true;
            }
        }

        return false;
    }

    // lay danh sach nuoc di hop le
    public boolean[][] getValidMoves(int player) {
        boolean[][] validMoves = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (NuocDiHopLe(i, j, player)) {
                    validMoves[i][j] = true;
                } else {
                    validMoves[i][j] = false;
                }
            }
        }
        return validMoves;
    }

    // lật các quân cờ đã bị lật
    private void latCacQuanCo(int row, int col) {
        int doiThu = (LuotChoiHienTai == BLACK) ? WHITE : BLACK;

        // duyet 8 huong
        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            // di theo huong nay
            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == doiThu) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            // co the lat duoc o huong nay
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

    // tinh diem
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

    // ket qua tran dau
    public String getGameResult() {
        if (blackScore > whiteScore) {
            return "ĐEN THẮNG! (" + blackScore + " - " + whiteScore + ")";
        } else if (whiteScore > blackScore) {
            return "TRẮNG THẮNG! (" + whiteScore + " - " + blackScore + ")";
        } else {
            return "HÒA! (" + blackScore + " - " + whiteScore + ")";
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public int getLuotChoiHienTai() {
        return LuotChoiHienTai;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }
}