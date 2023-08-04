package com.example.boardproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="board_reply")
public class BoardReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reply_seq")
    private Long replySeq;

    @Column(name="reply_content")
    private String replyContent;

    @CreatedDate
    @Column(name="reply_created_date")
    private LocalDateTime replyCreatedDate;

    @Column(name="reply_updated_date")
    private LocalDateTime replyUpdatedDate;

    @Column(name="reply_status")
    private boolean replyStatus;

    @Column(name="comment_seq")
    private Long commentSeq;

    @Column(name="board_seq")
    private Long boardSeq;

    @Column(name="user_seq")
    private Long userSeq;



}
