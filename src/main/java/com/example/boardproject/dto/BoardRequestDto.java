package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardEntity;
import lombok.*;
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

    private LocalDateTime boardCreatedDate;

    @LastModifiedDate
    private LocalDateTime boardUpdatedDate;

    private Long userSeq;

    @Transient
    private String userId;

    @Transient
    private String imagePath;

    @Transient
    private String imageName;

    //INSERT
    public BoardEntity toEntity() {
        return BoardEntity.builder()
                .boardSeq(this.boardSeq)
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .boardType(this.boardType)
                .commentCnt(this.commentCnt)
                .viewCnt(this.viewCnt)
                .likeCnt(this.likeCnt)
                .imageYn(this.imageYn)
                .boardStatus(this.boardStatus)
                .boardCreatedDate(this.boardCreatedDate)
                .boardUpdatedDate(this.boardUpdatedDate)
                .userSeq(this.userSeq)
                .build();

    }

    public BoardRequestDto(BoardResponseDto boardResponseDto) {
        this.boardSeq = boardResponseDto.getBoardSeq();
        this.boardTitle = boardResponseDto.getBoardTitle();
        this.boardStatus = boardResponseDto.isBoardStatus();
        this.boardType = boardResponseDto.getBoardType();
        this.boardContent = boardResponseDto.getBoardContent();
        this.commentCnt = boardResponseDto.getCommentCnt();
        this.viewCnt = boardResponseDto.getViewCnt();
        this.likeCnt = boardResponseDto.getLikeCnt();
        this.imageYn = boardResponseDto.isImageYn();
        this.boardCreatedDate = boardResponseDto.getBoardCreatedDate();
        this.userSeq = boardResponseDto.getUserSeq();
    }
}
