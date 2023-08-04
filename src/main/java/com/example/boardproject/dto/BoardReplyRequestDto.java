package com.example.boardproject.dto;

import com.example.boardproject.entity.BoardReplyEntity;
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
public class BoardReplyRequestDto {
    private Long replySeq;
    private String replyContent;
    private LocalDateTime replyCreatedDate;
    @LastModifiedDate
    private LocalDateTime replyUpdatedDate;
    private boolean replyStatus;
    private Long commentSeq;
    private Long boardSeq;
    private Long userSeq;

    @Transient
    private String userId;

    public BoardReplyEntity toEntity() {
        return BoardReplyEntity.builder()
                .replySeq(this.replySeq)
                .replyContent(this.replyContent)
                .replyCreatedDate(this.replyCreatedDate)
                .replyUpdatedDate(this.replyUpdatedDate)
                .replyStatus(this.replyStatus)
                .commentSeq(this.commentSeq)
                .boardSeq(this.boardSeq)
                .userSeq(this.userSeq)
                .build();
    }




}
