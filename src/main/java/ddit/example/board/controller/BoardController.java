package ddit.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ddit.example.board.service.BoardService;
import ddit.example.board.vo.BoardVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController
{
	@Autowired
	BoardService boardService;

	// 목록
	@GetMapping("/list")
	public String list(Model model)
	{
		model.addAttribute("boardList", boardService.readBoardList());
		return "board/list";
	}

	// 상세
	@GetMapping("/detail/{brdNo}")
	@PreAuthorize("isAuthenticated()") // SecurityConfig 의 anyRequest().authenticated() 가 이미 처리
	public String detail(@PathVariable(value = "brdNo") int brdNo, Model model)
	{
		model.addAttribute("boardDetail", boardService.readBoardDetail(brdNo));
		return "board/detail";
	}

	// 등록/수정
	@GetMapping("/form")
	public String form(@RequestParam(required = false) Integer brdNo, Model model)
	{
		// 등록
		if (brdNo == null)
		{
			model.addAttribute("board", new BoardVO());
		}
		// 수정
		else
		{
			model.addAttribute("board", boardService.readBoardDetail(brdNo));
		}
		return "board/form";
	}
}
