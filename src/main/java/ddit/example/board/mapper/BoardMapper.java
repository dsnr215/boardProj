package ddit.example.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ddit.example.board.vo.BoardVO;

@Mapper
public interface BoardMapper {
	
	// 목록
	List<BoardVO> selectBoardList();

	// 상세
	BoardVO selectBoardDetail(int brdNo);

	// 등록
	int insertBoard(BoardVO boardVO);

	// 수정
	int updateBoard(BoardVO boardVO);
	
	// 삭제 
	int deleteBoard(int brdNo);
}
