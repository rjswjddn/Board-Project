package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.BoardType;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class BoardResponseDto {
    private Long boardSeq;
    private String boardTitle;
    @Size(max = 2000, message = "내용은 최대 2000 byte까지 입력할 수 있습니다.")
    private String boardContent;
    private String boardType;
    private int commentCnt;
    private int viewCnt;
    private int likeCnt;
    private boolean imageYn;
    private boolean boardStatus;
    private LocalDateTime boardCreatedDate;
    private LocalDateTime boardUpdatedDate;
    private Long userSeq;
    private String userId;


    public BoardResponseDto(Long boardSeq, String boardTitle, String boardContent, String boardType, int commentCnt, int viewCnt, int likeCnt, boolean imageYn, boolean boardStatus,
                            LocalDateTime boardCreatedDate, LocalDateTime boardUpdatedDate, Long userSeq, String userId) {
        this.boardSeq = boardSeq;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardType = boardType;
        this.commentCnt = commentCnt;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.imageYn = imageYn;
        this.boardStatus = boardStatus;
        this.boardCreatedDate = boardCreatedDate;
        this.boardUpdatedDate = boardUpdatedDate;
        this.userSeq = userSeq;
        this.userId = userId;
    }

    public BoardType getBoardTypeEnum() {
        return BoardType.valueOf(boardType);
    }

    public void setBoardTypeEnum(BoardType boardType) {
        this.boardType = boardType.name();
    }

    public BoardResponseDto(BoardEntity boardEntity) {
        this.boardSeq = boardEntity.getBoardSeq();
        this.boardTitle = boardEntity.getBoardTitle();
        this.boardContent = boardEntity.getBoardContent();
        this.boardType = boardEntity.getBoardType();
        this.commentCnt = boardEntity.getCommentCnt();
        this.viewCnt = boardEntity.getViewCnt();
        this.likeCnt = boardEntity.getLikeCnt();
        this.imageYn = boardEntity.isImageYn();
        this.boardStatus = boardEntity.isBoardStatus();
        this.boardCreatedDate = boardEntity.getBoardCreatedDate();
        this.boardUpdatedDate = boardEntity.getBoardUpdatedDate();
        this.userSeq = boardEntity.getUserSeq();
    }



}
