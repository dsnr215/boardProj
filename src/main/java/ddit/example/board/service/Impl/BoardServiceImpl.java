package ddit.example.board.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ddit.example.board.mapper.BoardMapper;
import ddit.example.board.service.BoardService;
import ddit.example.board.vo.BoardVO;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	BoardMapper boardMapper;

	@Override
	public List<BoardVO> readBoardList() {
		return boardMapper.selectBoardList();
	}

	@Override
	public BoardVO readBoardDetail(int brdNo) {
		return boardMapper.selectBoardDetail(brdNo);
	}

	@Override
	public int createBoard(BoardVO boardVO) {
		return boardMapper.insertBoard(boardVO);
	}

	@Override
	public int updateBoard(BoardVO boardVO) {
		return boardMapper.updateBoard(boardVO);
	}

	@Override
	public int deleteBoard(int brdNo) {
		return boardMapper.deleteBoard(brdNo);
	}

}
