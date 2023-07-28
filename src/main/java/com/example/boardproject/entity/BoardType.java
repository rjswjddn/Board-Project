package com.example.boardproject.entity;

public enum BoardType {
    N("공지", "fas fa-bullhorn"), // N: NOTICE
    G("[일반]", null), // G: GENERAL
    S("[비밀]", "fas fa-key"); // S: SECRET

    private final String displayName;
    private final String iconClass;

    BoardType(String displayName, String iconClass) {
        this.displayName = displayName;
        this.iconClass = iconClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }
}
