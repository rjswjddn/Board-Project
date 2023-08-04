package com.example.boardproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class CommentReplyResponseDto {
    private Long boardSeq;
    private Long commentSeq;
    private String commentContent;
    private LocalDateTime commentCreatedDate;
    private boolean commentStatus;
    private Long userSeq;
    private Long replySeq;
    private String replyContent;
    private LocalDateTime replyCreatedDate;

    public CommentReplyResponseDto(Long boardSeq, Long commentSeq, String commentContent, LocalDateTime commentCreatedDate, boolean commentStatus,
                                   Long userSeq, Long replySeq, String replyContent, LocalDateTime replyCreatedDate) {
        this.boardSeq = boardSeq;
        this.commentSeq = commentSeq;
        this.commentContent = commentContent;
        this.commentCreatedDate = commentCreatedDate;
        this.commentStatus = commentStatus;
        this.userSeq = userSeq;
        this.replySeq = replySeq;
        this.replyContent = replyContent;
        this.replyCreatedDate = replyCreatedDate;
    }















}
