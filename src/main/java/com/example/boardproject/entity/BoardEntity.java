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
@Table(name="board")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_seq")
    private Long boardSeq;

    @Column(name="board_title", length = 45)
    private String boardTitle;

    @Column(name = "board_content", length = 3000)
    private String boardContent;

    @Column(name="board_type", nullable = false, length = 1)
    private String boardType;

//    @Column(name="board_type", nullable = false)
//    private BoardType boardType;

    @Column(name = "comment_cnt")
    private int commentCnt;

    @Column(name = "view_cnt")
    private int viewCnt;

    @Column(name = "like_cnt")
    private int likeCnt;

    @Column(name = "image_yn", columnDefinition = "BIT(1)")
    private boolean imageYn;

    @Column(name = "board_status", columnDefinition = "BIT(1)")
    private boolean boardStatus;

    @CreatedDate
    @Column(name = "board_created_date")
    private LocalDateTime boardCreatedDate;

    @Column(name = "board_updated_date")
    private LocalDateTime boardUpdatedDate;

    @Column(name = "user_seq")
    private Long userSeq;

    @Transient
    private boolean isNew;

}
