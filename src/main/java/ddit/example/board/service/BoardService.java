package ddit.example.board.service;

import java.util.List;

import ddit.example.board.vo.BoardVO;

public interface BoardService {
	
	// 목록
	List<BoardVO> readBoardList();

	// 상세
	BoardVO readBoardDetail(int brdNo);
	
	// 등록
	int createBoard(BoardVO boardVO);
	
	// 수정
	int updateBoard(BoardVO boardVO);

	// 삭제
	int deleteBoard(int brdNo);
}
