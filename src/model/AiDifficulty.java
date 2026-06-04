package model;

// UC-02 2.1.14: Enum định nghĩa mức độ khó AI (depth = độ sâu Minimax)
public enum AiDifficulty {
    EASY(2, "Dễ"),       // UC-02 2.1.14a: Minimax depth = 2
    NORMAL(5, "Bình thường"), // UC-02 2.1.14b: Minimax depth = 5
    HARD(8, "Khó");      // UC-02 2.1.14c: Minimax depth = 8

    private final int depth;
    private final String displayName;

    AiDifficulty(int depth, String displayName) {
        this.depth = depth;
        this.displayName = displayName;
    }

    public int getDepth() { return depth; }
    public String getDisplayName() { return displayName; }
}
