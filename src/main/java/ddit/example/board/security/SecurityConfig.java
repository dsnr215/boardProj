package ddit.example.board.security;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.DispatcherType;

//@formatter:off
/**
 * Spring Security 전체 보안 설정 클래스
 *
 * @Configuration  : 스프링 설정 클래스임을 선언 (빈 등록 전용)
 * @EnableWebSecurity     : Spring Security 활성화
 * @EnableMethodSecurity  : @PreAuthorize, @PostAuthorize 등 메서드 단위 권한 어노테이션 활성화
 */
@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
public class SecurityConfig {

    /** 사용자 인증 정보를 DB에서 조회하는 서비스 (DI) */
    @Autowired
    UserDetailsServiceImpl detailsServiceImpl;

    @Autowired
    private DataSource dataSource;

    // ─────────────────────────────────────────────
    // [1] 정적 리소스 보안 예외 처리
    // /static/** 경로는 Security 필터 자체를 거치지 않음
    // (이미지, CSS, JS 등 인증이 불필요한 리소스 대상)
    // ─────────────────────────────────────────────
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    // ─────────────────────────────────────────────
    // [2] 보안 필터 체인 구성 → 이전 대화에서 정리 완료
    // ─────────────────────────────────────────────
    // @formatter:off
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * HTTP 요청에 대한 보안 필터 체인 구성
         * 클라이언트 → [필터1 → 필터2 → 필터3] → 서버
         * 클라이언트 ← [필터1 ← 필터2 ← 필터3] ← 서버
         */
        return http
            // ── 기본 보안 설정 ──────────────────────────────────────────────────
            .csrf(csrf -> csrf.disable())           // CSRF 보호 비활성화
            .httpBasic(hbasic -> hbasic.disable())  // Spring Security 기본 제공 로그인 폼 비활성화

            // ── 프레임 설정(X-Frame-Options - iframe 허용 범위) ───────────────────
            // DENY        : iframe 완전 차단
            // SAMEORIGIN  : 동일 도메인만 허용  ← 현재 설정
            // ALLOW-FROM  : 특정 도메인만 허용
            .headers(config -> config
                .frameOptions(customizer -> customizer.sameOrigin())
            )

            // ── URL별 접근 권한 설정 ────────────────────────────────────────────
            .authorizeHttpRequests(authz -> authz
                // 서블릿 내부 포워드 / 비동기 요청은 인증 없이 허용
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ASYNC).permitAll()

                // 인증 없이 누구나 접근 가능한 경로
                .requestMatchers(
                    "/login", "/signup", "/user", "/error",
                    "/js/**", "/adminlte/**", "/images/**",
                    "/api/**", "/blog/**", "/**"
                ).permitAll()
                .anyRequest().authenticated()                 // 그 외 모든 요청은 로그인 필요
            )

            // ── 폼 로그인 설정 ──────────────────────────────────────────────────
            .formLogin(formLogin -> formLogin
                .loginPage("/login")                          // 로그인 페이지 (GET)
                .loginProcessingUrl("/login")                 // 로그인 처리 경로 (POST) - 누락 시 404 발생
                .successHandler(customLoginSuccessHandler())  // 로그인 성공 시 커스텀 핸들러 실행
            )

            // ── 세션 관리 ───────────────────────────────────────────────────────
            .sessionManagement(session -> session
                .maximumSessions(1)  // 동일 계정 동시 로그인 1개로 제한
            )

            // ── 로그아웃 설정 ───────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler())  // 로그아웃 성공 시 커스텀 핸들러 실행
                .invalidateHttpSession(true)                         // 세션 무효화
                .deleteCookies("JSESSIONID", "remember-me")         // 관련 쿠키 삭제
            )

            // ── 접근 거부 처리 (403) ────────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(customAccessDeniedHandler())
            )

            .build();
    }
    // @formatter:on

	// ─────────────────────────────────────────────
	// [3] 인증 관리자 (AuthenticationManager)
	//
	// 로그인 시 인증 처리 흐름:
	// 사용자 입력(email + pw)
	// → DaoAuthenticationProvider
	// → UserDetailsServiceImpl.loadUserByUsername() : DB에서 사용자 조회
	// → BCryptPasswordEncoder : 비밀번호 검증
	// → 인증 성공 시 SecurityContext에 Authentication 저장
	// ─────────────────────────────────────────────
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
			BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService detailsService)
	{

		// DaoAuthenticationProvider : DB 기반 인증 제공자
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(detailsServiceImpl); // 사용자 조회 서비스 지정
		authProvider.setPasswordEncoder(bCryptPasswordEncoder); // 비밀번호 인코더 지정

		return new ProviderManager(authProvider);
	}

	// ─────────────────────────────────────────────
	// [4] 비밀번호 인코더 Bean 등록
	// BCrypt : 단방향 해시 알고리즘 (복호화 불가, 매번 다른 해시값 생성)
	// 가입 시 암호화 저장 → 로그인 시 입력값과 DB 해시값을 matches()로 비교
	// ─────────────────────────────────────────────
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	// ─────────────────────────────────────────────
	// [5] 커스텀 핸들러 Bean 등록
	// Security 이벤트(로그인 성공, 로그아웃, 접근 거부) 발생 시
	// 기본 동작 대신 우리가 직접 만든 클래스로 처리
	// ─────────────────────────────────────────────

	/** 로그인 성공 시 실행 (예: 역할별 페이지 분기 리다이렉트) */
	@Bean
	public AuthenticationSuccessHandler customLoginSuccessHandler()
	{
		return new CustomLoginSuccessHandler();
	}

	/** 로그아웃 성공 시 실행 (예: 로그아웃 메시지 + 메인 페이지 이동) */
	@Bean
	public LogoutSuccessHandler customLogoutSuccessHandler()
	{
		return new CustomLogoutSuccessHandler();
	}

	/** 접근 거부(403) 시 실행 (예: 권한 없음 경고창 또는 에러 페이지 이동) */
	@Bean
	public AccessDeniedHandler customAccessDeniedHandler()
	{
		return new CustomAccessDeniedHandler();
	}
	// @formatter:on

}