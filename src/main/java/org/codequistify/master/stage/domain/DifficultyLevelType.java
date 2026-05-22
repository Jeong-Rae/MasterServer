package org.codequistify.master.stage.domain;

public enum DifficultyLevelType {
    L1(1, 100),
    L2(2, 120),
    L3(3, 156),
    L4(4, 219),
    L5(5, 327);

    private final int level;
    private final int exp;

    DifficultyLevelType(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }

    public int level() {
        return level;
    }

    public int exp() {
        return exp;
    }
}
