package com.example.boardproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="board_comment")
public class BoardCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_seq")
    private Long commentSeq;

    @Column(name="comment_content")
    private String commentContent;

    @CreatedDate
    @Column(name="comment_created_date")
    private LocalDateTime commentCreatedDate;

    @LastModifiedDate
    @Column(name="comment_updated_date")
    private LocalDateTime commentUpdatedDate;

    @Column(name="comment_status")
    private boolean commentStatus;

    @Column(name="board_seq")
    private Long boardSeq;

    @Column(name="user_seq")
    private Long userSeq;


}
