package model;

public class GameConfig {
    private final GameMode mode;
    private final AiDifficulty difficulty;

    public static GameConfig pvp() {
        return new GameConfig(GameMode.PVP, null);
    }

    // UC-02 2.1.12: Factory method tạo cấu hình PVE với độ khó được chọn
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

    // UC-02 2.1.13: Getter trả về mức độ khó AI đã chọn
    public AiDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean isAiEnabled() {
        return mode == GameMode.PVE;
    }
}
