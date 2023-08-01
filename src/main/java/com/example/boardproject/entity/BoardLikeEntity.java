package com.example.boardproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board_like")
public class BoardLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq")
    private Long likeSeq;

    @CreatedDate
    @Column(name = "like_created_date")
    private LocalDateTime likeCreatedDate;

    @Column(name = "board_seq")
    private Long boardSeq;

    @Column(name = "user_seq")
    private Long userSeq;

}
