package ddit.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ddit.example.board.service.BoardService;
import ddit.example.board.vo.BoardVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/board")
public class BoardApiController
{

	@Autowired
	BoardService boardService;

	// 등록 (POST)
	@PostMapping
	public ResponseEntity<Object> insert(@Validated @RequestBody BoardVO boardVO, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails)
	{
		// 로그인한 사용자 ID 세팅
		boardVO.setWriterId(userDetails.getUsername());
		boardService.createBoard(boardVO);

		return ResponseEntity.ok(boardVO.getBrdNo());
	}

	// 수정 (PUT)
	@PutMapping("/{brdNo}")
	public ResponseEntity<Object> update(@PathVariable int brdNo, @Validated @RequestBody BoardVO boardVO,
			BindingResult bindingResult)
	{

		// 유효성 검사 실패 시 400 반환
		if (bindingResult.hasErrors())
		{
			String errorMsg = bindingResult.getFieldErrors().stream().map(e -> e.getDefaultMessage())
					.collect(Collectors.joining(", "));
			return ResponseEntity.badRequest().body(errorMsg);
		}

		boardVO.setBrdNo(brdNo);
		boardService.updateBoard(boardVO);

		return ResponseEntity.ok(brdNo);
	}

	// 삭제 (DELETE)
	@DeleteMapping("/{brdNo}")
	public ResponseEntity<Object> delete(@PathVariable int brdNo)
	{
		boardService.deleteBoard(brdNo);
		return ResponseEntity.ok(brdNo);
	}
}