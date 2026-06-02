package model;

public enum AiDifficulty {
    EASY(2, "Dễ"),
    NORMAL(5, "Bình thường"),
    HARD(8, "Khó");

    private final int depth;
    private final String displayName;

    AiDifficulty(int depth, String displayName) {
        this.depth = depth;
        this.displayName = displayName;
    }

    public int getDepth() { return depth; }
    public String getDisplayName() { return displayName; }
}
