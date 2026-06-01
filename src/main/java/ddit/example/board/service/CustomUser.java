package ddit.example.board.service;

import org.springframework.boot.security.autoconfigure.SecurityProperties.User;

// [사용자 정의 인증 객체]
// - User(스프링 시큐리티 제공) 을 상속받아 usersVO 를 함께 보관
// - 로그인 성공 후 principal = CustomUser 객체가 됨 → Controller에서 auth.getPrincipal()로 꺼내 UsersVO에 접근 가능

public class CustomUser extends User
{

}
