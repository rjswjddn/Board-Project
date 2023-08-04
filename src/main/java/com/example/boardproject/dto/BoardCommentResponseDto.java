package com.example.boardproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardCommentResponseDto {
    private Long commentSeq;
    private String commentContent;
    private LocalDateTime commentCreatedDate;
    private LocalDateTime commentUpdatedDate;
    private boolean commentStatus;
    private Long boardSeq;
    private Long userSeq;
    private String userId;
}
