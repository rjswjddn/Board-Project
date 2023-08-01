package com.example.boardproject.entity;

public enum BoardType {
    N("공지글", "fa-solid fa-bullhorn fa-beat"), // N: NOTICE
    G("일반글", null), // G: GENERAL
    S("비밀글", "fa-solid fa-key fa-shake"); // S: SECRET

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
