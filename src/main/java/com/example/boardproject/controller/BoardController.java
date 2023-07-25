package com.example.boardproject.controller;

import com.example.boardproject.dto.BoardRequestDto;
import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.service.BoardService;
import com.example.boardproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @GetMapping("/board")
    public String board(@RequestParam(name = "page", defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        List<BoardResponseDto> noticeBoards = boardService.getNoticeBoardsWithUsers();
        Page<BoardResponseDto> normalBoards = boardService.getAllNormalBoardsWithUsers(pageable);

        // 공지글 최대 5개
        int maxNoticeCount = 5;
        if (noticeBoards.size() > maxNoticeCount) {
            noticeBoards = noticeBoards.subList(0, maxNoticeCount);
        }

        // 첫페이지 공지글과 일반글 합쳐서 모델에 추가
        List<BoardResponseDto> allBoards = new ArrayList<>();
        if (page == 0) {
            allBoards.addAll(noticeBoards);
        }
        allBoards.addAll(normalBoards.getContent());

        model.addAttribute("Boards", allBoards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", normalBoards.getTotalPages());
        return "board_list";
    }


    //검색 기능
    @GetMapping("/search")
    public String search(@RequestParam(name = "searchType", defaultValue = "boardTitle") String searchType,
                         @RequestParam(name = "keyword") String keyword,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("boardSeq").descending());

        Page<BoardResponseDto> searchResult;
        if ("boardTitle".equals(searchType)) {
            searchResult = boardService.searchByTitle(keyword, pageable);
        } else if ("userSeq".equals(searchType)) {
            searchResult = boardService.searchByUserId(keyword, pageable);
        } else if ("boardContent".equals(searchType)) {
            searchResult = boardService.searchByContent(keyword, pageable);
        } else {
            searchResult = new PageImpl<>(Collections.emptyList());
        }

        // isNew(신규 등록) 필드 설정
        LocalDateTime now = LocalDateTime.now();
        for (BoardResponseDto boardDto : searchResult.getContent()) {
            if (Duration.between(boardDto.getBoardCreatedDate(), now).toHours() < 24) {
                boardDto.setIsNew(true);
            } else {
                boardDto.setIsNew(false);
            }
        }
        model.addAttribute("Boards", searchResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        return "board_list";
    }

    @GetMapping("board/register")
    public String registerBoardView(Model model) {
        model.addAttribute("boardRequestDto", new BoardRequestDto());
        return "board_register";
    }

    //    @RequestParam("image") MultipartFile imageFile,
    @PostMapping("/board/register")
    public String registerBoard(@ModelAttribute("boardRequestDto") BoardRequestDto boardRequestDto,
                                @RequestParam("boardTypeInput") String boardType) {
        try {
            boardRequestDto.setBoardType(boardType);
            boardService.registerBoard(boardRequestDto);
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
        }
        return "redirect:/board";
    }


    @GetMapping("board_view")
    public String viewBoard(@RequestParam("boardseq") Long boardSeq, Model model, HttpServletRequest httpServletRequest) {
        BoardResponseDto boardResponseDto = boardService.findByBoardSeq(boardSeq);

        // 게시물 등록 유저 정보
        UserDto userDto = userService.findByUserSeq(boardResponseDto.getUserSeq());
        String boardUserId = userDto.getUserId();

        // 현재 로그인 중인 유저 정보
        HttpSession session = httpServletRequest.getSession();
        String loginId = (String) session.getAttribute("userId");

        // 같으면 true 다르면 false
        boolean boardAuth = loginId.equals(boardUserId);

        model.addAttribute("boardResponseDto", boardResponseDto);
        model.addAttribute("boardUserId", boardUserId);
        model.addAttribute("boardAuth", boardAuth);
        return "board_view";
    }

    // 게시물 삭제
    @GetMapping("board/delete")
    public String deleteBoard(Model model, @RequestParam("boardseq") Long boardSeq, @RequestParam("boarduserid") String boardUserId, HttpServletRequest httpServletRequest) {
        // 로그인 유저와 게시물 유저가 같은지 확인
        HttpSession session = httpServletRequest.getSession();
        if (!session.getAttribute("userId").equals(boardUserId)) {
            return "board_list";
        }

        boardService.deleteBoard(boardSeq);

        model.addAttribute("message", "게시글이 삭제 되었습니다.");
        model.addAttribute("url", "/board");
        return "alert";
    }

    // 게시물 업데이트 페이지 이동
    @GetMapping("board/update")
    public String updateBoardPage(Model model, @RequestParam("boardseq") Long boardSeq, @RequestParam("boarduserid") String boardUserId, HttpServletRequest httpServletRequest) {
        // 로그인 유저와 게시물 유저가 같은지 확인
        HttpSession session = httpServletRequest.getSession();
        if (!session.getAttribute("userId").equals(boardUserId)) {
            return "board_list";
        }




        BoardResponseDto boardResponseDto = boardService.findByBoardSeq(boardSeq);
        model.addAttribute("boardResponseDto", boardResponseDto);
        model.addAttribute("boardUserId", boardUserId);
        return "board_update";
    }

    // 게시물 업데이트 적용
    @PostMapping("board/update")
    public String updateBoard(@RequestParam("boardseq") Long boardSeq, @ModelAttribute("boardResponseDto") BoardResponseDto boardResponseDto) {
        BoardRequestDto boardRequestDto = new BoardRequestDto(boardResponseDto);
        boardService.updateBoard(boardSeq, boardRequestDto);

        return "redirect:/board_view?boardseq=" + boardSeq;
    }

}
