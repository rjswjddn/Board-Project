package com.example.boardproject.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class BoardCommentResponseDto {
    private Long commentSeq;
    private String commentContent;
    private LocalDateTime commentCreatedDate;
    private LocalDateTime commentUpdatedDate;
    private boolean commentStatus;
    private Long boardSeq;
    private Long userSeq;
    private String userId;
    private List<BoardReplyResponseDto> boardReplyResponseDtoList;

    public BoardCommentResponseDto(Long commentSeq, String commentContent, LocalDateTime commentCreatedDate, LocalDateTime commentUpdatedDate,
                                   boolean commentStatus, Long boardSeq, Long userSeq, String userId){
        this.commentSeq = commentSeq;
        this.commentContent = commentContent;
        this.commentCreatedDate = commentCreatedDate;
        this.commentUpdatedDate = commentUpdatedDate;
        this.commentStatus = commentStatus;
        this.boardSeq = boardSeq;
        this.userSeq = userSeq;
        this.userId = userId;
        boardReplyResponseDtoList = new ArrayList<>();

    }





}
