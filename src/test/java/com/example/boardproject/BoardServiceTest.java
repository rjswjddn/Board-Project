package com.example.boardproject;

import com.example.boardproject.repository.*;
import com.example.boardproject.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardServiceTest {
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardImageRepository boardImageRepository;

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private BoardCommentRepository boardCommentRepository;

    @Mock
    private BoardReplyRepository boardReplyRepository;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // 목 객체 초기화
    }

    @Test
    public void testToggleLikeForNonExistingBoard() {
            Long nonExistingBoardSeq = Long.MAX_VALUE; // 존재하지 않는 게시물의 boardSeq
            Long userSeq = 12345L; // 존재하는 사용자의 userSeq
            BoardService boardService = new BoardService(boardRepository, userRepository, boardImageRepository, boardLikeRepository, boardCommentRepository, boardReplyRepository); // 목 객체를 주입한 서비스 객체 생성

            IOException exception = assertThrows(IOException.class, () -> {
                boardService.toggleLike(nonExistingBoardSeq, userSeq);
            });
            assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
        }











    }

