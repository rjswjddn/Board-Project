package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardRequestDto {
    private Long boardSeq;

    private String boardTitle;

    private String boardContent;

    private String boardType;

    private int commentCnt;

    private int viewCnt;

    private int likeCnt;

    private Boolean imageYn = false;

    private Boolean boardStatus = false;

    @CreatedDate
    private LocalDateTime boardCreatedDate;

    @LastModifiedDate
    private LocalDateTime boardUpdatedDate;

    private Long userSeq;

    @Transient
    private String imagePath;

    //꺼내올때
//    public BoardRequestDto(BoardEntity boardEntity){
//        this.boardSeq=boardEntity.getBoardSeq();
//        this.boardTitle=boardEntity.getBoardTitle();
//        this.boardStatus = BoardStatus.valueOf(boardEntity.getBoardStatus());
//        this.boardContent=boardEntity.getBoardContent();
//        this.commentCnt=boardEntity.getCommentCnt();
//        this.viewCnt=boardEntity.getViewCnt();
//        this.likeCnt=boardEntity.getLikeCnt();
//        this.imageYn=boardEntity.isImageYn();
//        this.deleteYn=boardEntity.isDeleteYn();
//        this.boardCreatedDate=boardEntity.getBoardCreatedDate();
//
//        this.userSeq=boardEntity.getUserSeq();
//    }

    //INSERT
    public BoardEntity toEntity() {
        return BoardEntity.builder()
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .boardType(this.boardType)
                .commentCnt(this.commentCnt)
                .viewCnt(this.viewCnt)
                .likeCnt(this.likeCnt)
                .imageYn(this.imageYn)
                .boardStatus(this.boardStatus)
                .boardCreatedDate(this.boardCreatedDate)
                .userSeq(this.userSeq)
                .build();

    }
}
