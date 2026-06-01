package ddit.example.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import ddit.example.board.vo.UsersVO;

@Mapper
public interface UsersMapper
{
	public UsersVO findByUserId(String userId);
}
