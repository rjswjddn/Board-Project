package com.example.boardproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardReplyResponseDto {
    private Long replySeq;
    private String replyContent;
    private LocalDateTime replyCreatedDate;
    private LocalDateTime replyUpdatedDate;
    private boolean replyStatus;
    private Long commentSeq;
    private Long boardSeq;
    private String userId;

    public BoardReplyResponseDto(Long replySeq, String replyContent, LocalDateTime replyCreatedDate, LocalDateTime replyUpdatedDate, boolean replyStatus,
                                 Long commentSeq, Long boardSeq, String userId) {
        this.replySeq = replySeq;
        this.replyContent = replyContent;
        this.replyCreatedDate = replyCreatedDate;
        this.replyUpdatedDate = replyUpdatedDate;
        this.replyStatus = replyStatus;
        this.commentSeq = commentSeq;
        this.boardSeq = boardSeq;
        this.userId = userId;
    }



}

