package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardCommentEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardCommentRequestDto {
    private Long commentSeq;
    private String commentContent;
    @CreatedDate
    private LocalDateTime commentCreatedDate;
    private LocalDateTime commentUpdatedDate;
    private boolean commentStatus;
    private Long boardSeq;
    private Long userSeq;

    @Transient
    private String userId;

    public BoardCommentEntity toEntity() {
        return BoardCommentEntity.builder()
                .commentSeq(this.commentSeq)
                .commentContent(this.commentContent)
                .commentCreatedDate(this.commentCreatedDate)
                .commentStatus(this.commentStatus)
                .boardSeq(this.boardSeq)
                .userSeq(this.userSeq)
                .build();
    }


}
