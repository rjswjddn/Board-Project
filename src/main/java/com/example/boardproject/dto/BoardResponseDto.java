package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.BoardType;
import jakarta.persistence.Transient;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    private Long boardSeq;
    private String boardTitle;
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

    @Transient
    private boolean isNew;

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

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }


//    private BoardResponseDto convertToBoardResponseDto(BoardEntity boardEntity) {
//        BoardResponseDto boardResponseDto = BoardResponseDto.builder()
//                .boardSeq(boardEntity.getBoardSeq())
//                .boardTitle(boardEntity.getBoardTitle())
//                .boardType(BoardType.valueOf(boardEntity.getBoardType()))
//                .boardContent(boardEntity.getBoardContent())
//                .commentCnt(boardEntity.getCommentCnt())
//                .viewCnt(boardEntity.getViewCnt())
//                .likeCnt(boardEntity.getLikeCnt())
//                .imageYn(boardEntity.isImageYn())
//                .boardStatus(boardEntity.isBoardStatus())
//                .boardCreatedDate(boardEntity.getBoardCreatedDate())
//                .userSeq(boardEntity.getUserSeq())
//                .build();
//        boardResponseDto.setUserId(null);
//
//        return boardResponseDto;
//    }



}
