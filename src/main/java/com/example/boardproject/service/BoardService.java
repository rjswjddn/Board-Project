package com.example.boardproject.service;

import com.example.boardproject.dto.BoardRequestDto;
import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.UserEntity;
import com.example.boardproject.repository.BoardRepository;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 공지글
    public List<BoardResponseDto> getNoticeBoardsWithUsers() {
        List<BoardResponseDto> noticeBoards = boardRepository.findNoticeBoardsWithUsers();

        // 현재 시간 구하기
        LocalDateTime currentTime = LocalDateTime.now();

        // 공지글 중에서 24시간 이내의 글은 isNew 값을 true로 설정
        for (BoardResponseDto boardResponseDto : noticeBoards) {
            LocalDateTime boardCreatedDate = boardResponseDto.getBoardCreatedDate();
            if (boardCreatedDate != null) {
                Duration duration = Duration.between(boardCreatedDate, currentTime);
                if (duration.toHours() < 24) {
                    boardResponseDto.setIsNew(true);
                }
            }
        }
        return noticeBoards;
    }

    // 일반글 페이징 조회
    public Page<BoardResponseDto> getAllNormalBoardsWithUsers(Pageable pageable) {
        Page<BoardResponseDto> normalBoards = boardRepository.findNormalBoardsWithUsers(pageable);

        LocalDateTime currentTime = LocalDateTime.now();

        for (BoardResponseDto boardResponseDto : normalBoards.getContent()) {
            LocalDateTime boardCreatedDate = boardResponseDto.getBoardCreatedDate();
            if (boardCreatedDate != null) {
                Duration duration = Duration.between(boardCreatedDate, currentTime);
                if (duration.toHours() < 24) {
                    boardResponseDto.setIsNew(true);
                }
            }
        }
        return normalBoards;
    }

    public Page<BoardResponseDto> searchByTitle(String title, Pageable pageable) {
        return boardRepository.findByBoardTitleContainingIgnoreCaseOrderByBoardSeqDesc(title, pageable);
    }

    public Page<BoardResponseDto> searchByContent(String content, Pageable pageable) {
        return boardRepository.findByBoardContentContainingIgnoreCaseOrderByBoardSeqDesc(content, pageable);
    }

    public Page<BoardResponseDto> searchByUserId(String userId, Pageable pageable) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user != null) {
            return boardRepository.findBoardByUserSeqWithUserIdOrderByBoardSeqDesc(user.getUserSeq(), pageable);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Transactional
    public void registerBoard(BoardRequestDto boardRequestDto) throws IOException {
        // 이미지 파일 업로드 처리
//        String filePath = null;
//        if (!imageFile.isEmpty()) {
//            try {
//                // 파일 크기 확인
//                if (imageFile.getSize() > 20 * 1024 * 1024) { // 20MB 제한
//                    throw new IOException("업로드 가능한 파일 크기를 초과하였습니다.");
//                }
//
//                // 파일 확장자 확인 (이미지 파일 체크)
//                String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
//                String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
//                boolean isAllowedExtension = Arrays.stream(allowedExtensions).anyMatch(extension -> extension.equals(fileExtension));
//                if (!isAllowedExtension) {
//                    throw new IOException("이미지 파일만 업로드 가능합니다.");
//                }
//                // 이미지 파일 저장 경로 설정
//                String uploadDir = "/path/to/upload/directory/";
//                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
//                filePath = uploadDir + fileName;
//                boardRequestDto.setImagePath(filePath);
//                boardRequestDto.setBoardType(boardType);
//
//            } catch (IOException e) {
//
//                e.printStackTrace();
//
//            }
//        }
        BoardEntity boardEntity = boardRequestDto.toEntity();
        log.info("boardRequestDto->boardEntity: {}", boardEntity);

        boardRepository.save(boardEntity);
    }


    // Seq로 게시물 찾고 dto로 변환하여 return
    public BoardResponseDto findByBoardSeq(Long boardSeq) {
        BoardResponseDto boardResponseDto = new BoardResponseDto(boardRepository.findByBoardSeq(boardSeq));
        return boardResponseDto;
    }

    // Seq로 게시물을 찾아 board_status를 0으로 변경
    public void deleteBoard(Long boardSeq) {
        boardRepository.deleteByBoardSeq(boardSeq);
    }

    // 게시물 찾아 수정하고 저장
    public void updateBoard(Long boardSeq, BoardRequestDto boardRequestDto) {
        BoardEntity boardEntity = boardRepository.findByBoardSeq(boardSeq);
        boardEntity.setBoardType(boardRequestDto.getBoardType());
        boardEntity.setBoardTitle(boardRequestDto.getBoardTitle());
        boardEntity.setBoardContent(boardRequestDto.getBoardContent());

        boardRepository.save(boardEntity);
    }


}
