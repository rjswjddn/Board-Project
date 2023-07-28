package com.example.boardproject.controller;

import com.example.boardproject.dto.BoardRequestDto;
import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.service.BoardService;
import com.example.boardproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    //게시판 페이지
    @GetMapping("/board")
    public String board(@RequestParam(name = "page", defaultValue = "0") int page,
                        Model model, HttpSession httpSession) {

        // 로그인한 사용자의 아이디 가져오기
        String userId = (String) httpSession.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 사용자의 user_admin 값에 따라서 Thymeleaf 템플릿에 전달할 값을 설정
        boolean isAdmin = userService.getUserAdminByUserId(userId) == 1;
        model.addAttribute("isAdmin", isAdmin);

        Pageable pageable = PageRequest.of(page, 10);
        Page<BoardResponseDto> normalBoards = boardService.getAllNormalBoardsWithUsers(pageable);

        // 첫 페이지는 공지글과 일반글 합쳐서 모델에 추가
        List<BoardResponseDto> allBoards = new ArrayList<>();
        if (page == 0) {
            List<BoardResponseDto> noticeBoards = boardService.getNoticeBoardsWithUsers();
            allBoards.addAll(noticeBoards);
        }
        allBoards.addAll(normalBoards.getContent());

        model.addAttribute("Boards", allBoards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", normalBoards.getTotalPages());
        model.addAttribute("isSearch", false);

        return "board_list";
    }

    //검색 기능
    @GetMapping("/search")
    public String search(@RequestParam(name = "searchType", defaultValue = "boardTitle") String searchType,
                         @RequestParam(name = "keyword") String keyword,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {
        int pageSize = 10;
        Page<BoardResponseDto> searchResult = boardService.searchBoards(searchType, keyword, page, pageSize);

        model.addAttribute("Boards", searchResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("isSearch", true);

        return "board_list";
    }


    //게시물 작성 페이지
    @GetMapping("board/register")
    public String registerBoardView(Model model, HttpSession httpSession) {

        // 로그인한 사용자의 아이디 가져오기
        String loggedInUserId = (String) httpSession.getAttribute("userId");
        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        // 세션에서 관리자 여부 가져오기
        int isAdmin = (int) httpSession.getAttribute("admin");

        // 사용자의 user_admin 값에 따라서 Thymeleaf 템플릿에 전달할 값을 설정
//        boolean isAdmin = userService.getUserAdminByUserId(loggedInUserId) == 1;
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userId", loggedInUserId);

        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setUserId(loggedInUserId);
        model.addAttribute("boardRequestDto", boardRequestDto);

        return "board_register";
    }

    //게시물 작성 기능
    @PostMapping("/board/register")
    public String registerBoard(@Valid @ModelAttribute("boardRequestDto") BoardRequestDto boardRequestDto,
                                @RequestParam("boardTypeInput") String boardType,
                                @RequestParam("image") MultipartFile file,
                                BindingResult bindingResult,
                                HttpServletRequest httpServletRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            boardService.validateBoardRequest(boardRequestDto, bindingResult);
            if (bindingResult.hasErrors()) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    log.error("Validation error in field '{}' with value '{}'. Error message: {}",
                            error.getField(), error.getRejectedValue(), error.getDefaultMessage());
                }
                return "redirect:/board/register";
            }

            // 작성 페이지에서 세션에 저장된 로그인한 사용자의 아이디를 가져와서 boardRequestDto에 설정
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) {
                String userId = (String) session.getAttribute("userId");
                Long userSeq = userService.findUserSeqByUserId(userId); // userId로 userSeq를 찾아옴
                boardRequestDto.setUserId(userId);
                boardRequestDto.setUserSeq(userSeq);
            }

            log.info("게시글 작성 요청: {}", boardRequestDto);
            boardRequestDto.setBoardType(boardType);

            // 요청받은 boardType이 "N"인 경우 관리자 여부를 확인
            if ("N".equals(boardType) && session != null) {
                int isAdmin = (int) session.getAttribute("admin");
                if (isAdmin != 1) {
                    String userId = (String) session.getAttribute("userId");
                    log.error("관리자가 아닌 사용자 (" + userId + ")가 공지글을 작성하려고 시도했습니다.");
                    redirectAttributes.addFlashAttribute("errorMessage", "관리자만 공지글을 작성할 수 있습니다.");
                    return "redirect:/board/register";
                }
            }
            boardService.registerBoard(boardRequestDto, file);

        } catch (IOException e) {
            log.error("Error occurred during board registration: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/register";
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
