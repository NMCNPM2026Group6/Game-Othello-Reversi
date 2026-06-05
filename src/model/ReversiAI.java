package model;

import java.util.ArrayList;
import java.util.List;

public class ReversiAI {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int aiPlayer;
    private int[] bestMove;
    private int maxDepth = 5;

    private final int[] dx = { 0, 0, 1, -1, -1, 1, 1, -1 };
    private final int[] dy = { 1, -1, 0, 0, 1, 1, -1, -1 };

    public ReversiAI(int aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.bestMove = new int[2];
    }

    public void setDifficulty(AiDifficulty difficulty) {
        this.maxDepth = difficulty.getDepth();
    }

    // UC-05: Thuật toán Minimax tìm nước đi tốt nhất
    public int minimax(boolean maxmin, int[][] state, int depth, int player, int alpha, int beta) {
        if (depth == 0 || isOver(state)) {
            return heuristic(state);
        }

        List<int[]> validMoves = getValidMoves(state, player);

        if (validMoves.isEmpty()) {
            return minimax(!maxmin, state, depth - 1, getOpponent(player), alpha, beta);
        }

        if (maxmin) {
            int temp = -999999999;
            for (int[] move : validMoves) {
                int[][] newState = copyBoard(state);
                makeMove(newState, move[0], move[1], player);

                int value = minimax(false, newState, depth - 1, getOpponent(player), alpha, beta);

                if (value > temp) {
                    temp = value;
                    if (depth == maxDepth) {
                        bestMove[0] = move[0];
                        bestMove[1] = move[1];
                    }
                }
                alpha = Math.max(alpha, temp);
                if (beta <= alpha)
                    break;
            }
            return temp;
        } else {
            int temp = 999999999;
            for (int[] move : validMoves) {
                int[][] newState = copyBoard(state);
                makeMove(newState, move[0], move[1], player);

                int value = minimax(true, newState, depth - 1, getOpponent(player), alpha, beta);

                if (value < temp) {
                    temp = value;
                }
                beta = Math.min(beta, temp);
                if (alpha >= beta)
                    break;
            }
            return temp;
        }
    }

    public int[] findBestMove(int[][] board) {
        bestMove = new int[2];
        minimax(true, board, maxDepth, aiPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
    }

    private int heuristic(int[][] state) {
        int opponent = getOpponent(aiPlayer);
        int score = 0;

        int[][] positionWeight = {
                { 100, -20, 10, 5, 5, 10, -20, 100 },
                { -20, -50, -2, -2, -2, -2, -50, -20 },
                { 10, -2, 1, 1, 1, 1, -2, 10 },
                { 5, -2, 1, 0, 0, 1, -2, 5 },
                { 5, -2, 1, 0, 0, 1, -2, 5 },
                { 10, -2, 1, 1, 1, 1, -2, 10 },
                { -20, -50, -2, -2, -2, -2, -50, -20 },
                { 100, -20, 10, 5, 5, 10, -20, 100 }
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state[i][j] == aiPlayer) {
                    score += positionWeight[i][j];
                } else if (state[i][j] == opponent) {
                    score -= positionWeight[i][j];
                }
            }
        }

        int aiMoves = getValidMoves(state, aiPlayer).size();
        int opponentMoves = getValidMoves(state, opponent).size();
        score += (aiMoves - opponentMoves) * 5;

        return score;
    }

    private boolean isOver(int[][] state) {
        boolean blackCanMove = !getValidMoves(state, BLACK).isEmpty();
        boolean whiteCanMove = !getValidMoves(state, WHITE).isEmpty();
        return !blackCanMove && !whiteCanMove;
    }

    private List<int[]> getValidMoves(int[][] state, int player) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(state, i, j, player)) {
                    moves.add(new int[] { i, j });
                }
            }
        }
        return moves;
    }

    private boolean isValidMove(int[][] state, int row, int col, int player) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || state[row][col] != EMPTY) {
            return false;
        }

        int opponent = getOpponent(player);

        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == opponent) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == player) {
                return true;
            }
        }

        return false;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    private void makeMove(int[][] state, int row, int col, int player) {
        state[row][col] = player;
        int opponent = getOpponent(player);

        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == opponent) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == player) {
                int flipR = row + dx[i];
                int flipC = col + dy[i];
                while (flipR != r || flipC != c) {
                    state[flipR][flipC] = player;
                    flipR += dx[i];
                    flipC += dy[i];
                }
            }
        }
    }

    private int getOpponent(int player) {
        return (player == BLACK) ? WHITE : BLACK;
    }
}
