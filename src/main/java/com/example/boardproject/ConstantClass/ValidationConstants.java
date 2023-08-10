package com.example.boardproject.ConstantClass;

public class ValidationConstants {
    public static final int MAX_BOARD_TITLE_LENGTH = 45;
    public static final int MAX_BOARD_CONTENT_LENGTH = 2000;
    public static final String BOARD_TITLE_ERROR_MESSAGE = "제목은 최소 1자에서 최대 " + MAX_BOARD_TITLE_LENGTH + "자까지 허용됩니다.";
    public static final String BOARD_CONTENT_ERROR_MESSAGE = "내용은 최소 1자에서 최대 " + MAX_BOARD_CONTENT_LENGTH + "자까지 허용됩니다.";

    public static final int MAX_COMMENT_REPLY_CONTENT_LENGTH = 200;
    public static final String COMMENT_CONTENT_REPLY_ERROR_MESSAGE = "댓글과 답글은 최소 1자에서 최대 " + MAX_COMMENT_REPLY_CONTENT_LENGTH + "자까지 허용됩니다.";

}
