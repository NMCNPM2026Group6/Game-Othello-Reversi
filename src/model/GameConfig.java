package model;

public class GameConfig {
    private final GameMode mode;
    private final AiDifficulty difficulty;

    // UC-01 1.1.11: Factory method tạo cấu hình PVP (2 người chơi)
    public static GameConfig pvp() {
        return new GameConfig(GameMode.PVP, null);
    }

    public static GameConfig pve(AiDifficulty difficulty) {
        return new GameConfig(GameMode.PVE, difficulty);
    }

    private GameConfig(GameMode mode, AiDifficulty difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;
    }

    public GameMode getMode() {
        return mode;
    }

    public AiDifficulty getDifficulty() {
        return difficulty;
    }

    // UC-01 1.1.12: Kiểm tra chế độ AI hay PVP
    public boolean isAiEnabled() {
        return mode == GameMode.PVE;
    }
}
