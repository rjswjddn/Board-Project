package com.example.boardproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board_image")
public class BoardImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_seq")
    private Long imageSeq;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "board_seq")
    private Long boardSeq;

}
