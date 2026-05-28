package ddit.example.board.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import ddit.example.board.mapper.UsersMapper;
import ddit.example.board.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

// 스프링 시큐리티가 로그인 시 사용자 정보를 조회할 때 사용하는 서비스 구현체
// UserDetailsService 인터페이스의 loadUserByUsername() 메서드를 반드시 재정의해야 함
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	UsersMapper usersMapper;

	/**
	 * loadUserByUsername : 로그인 폼에서 username(= email)을 입력하면
	 *                      스프링 시큐리티가 자동으로 이 메서드를 호출함
	 *
	 * @param email 로그인 폼의 <input type="text" name="username"> 에 입력된 값
	 * @return CustomUser (UserDetails 구현체) → 스프링 시큐리티가 이 객체로 인증/인가를 관리
	 *         사용자가 없으면 null 반환 → 시큐리티가 인증 실패 처리
	 */
	@Override
	public UserDetails loadUserByUsername(String email)
	{

		log.info("loadUserByUsername - email : {}", email);

		// 1. DB에서 이메일로 회원 정보 조회
		UsersVO usersVO = usersMapper.findByEmail(email);
		log.info("loadUserByUsername - usersVO : {}", usersVO);

		/*
		 * 2. UsersVO(우리) → User(시큐리티) 필드 매핑 후 CustomUser로 반환
		 *    email       → username
		 *    password    → password
		 *    enabled     → enabled
		 *    authorities → authorities
		 *
		 * CustomUser : User를 상속받아 UsersVO를 함께 보관하는 클래스
		 *   → Controller에서 auth.getPrincipal()로 꺼내 UsersVO에 접근 가능
		 */
		return usersVO == null ? null : new CustomUser(usersVO);
	}

}