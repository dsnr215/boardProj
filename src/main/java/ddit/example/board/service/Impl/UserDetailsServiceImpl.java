package ddit.example.board.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import ddit.example.board.mapper.UsersMapper;
import ddit.example.board.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/** [Spring Security 로그인 처리 서비스]
 *
 * 로그인 시 Security가 자동으로 loadUserByUsername()을 호출함
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	UsersMapper usersMapper;

	/**
	 * 로그인 폼의 username(userId)으로 사용자 정보를 조회해 Security에 반환
	 *
	 * @param  email 로그인 폼 input[name="username"]에 입력된 값
	 * @return CustomUser (인증 성공) / null (사용자 없음 → Security가 인증 실패 처리)
	 *
	 * CustomUser는 User를 상속받아 UsersVO를 함께 보관하므로
	 * Controller에서 auth.getPrincipal()로 UsersVO에 접근 가능
	 */
	@Override
	public UserDetails loadUserByUsername(String userId)
	{

		log.info("loadUserByUsername - email : {}", userId);

		// 1. DB에서 userId로 회원 정보 조회
		UsersVO usersVO = usersMapper.findByUserId(userId);
		log.info("loadUserByUsername - usersVO : {}", usersVO);

		// UsersVO → CustomUser 변환 (email/password/enabled/authorities 매핑)
		// CustomUser : User를 상속받아 UsersVO를 함께 보관하는 클래스
		// → Controller에서 auth.getPrincipal()로 꺼내 UsersVO에 접근 가능
		return usersVO == null ? null : new CustomUser(usersVO);
	}

}