package com.eumpyo.eum.config;

import com.eumpyo.eum.config.oauth2.CustomOAuth2UserService;
import com.eumpyo.eum.config.oauth2.handler.CustomAuthSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// 스프링 시큐리티를 사용하여 보안 구성을 정의
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomAuthSuccessHandler customAuthSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS를 활성화하면, 다른 도메인에서의 리소스 요청을 처리할 수 있도록 서버의 응답 헤더에 Access-Control-Allow-Origin 헤더를 추가합니다.
        http.cors()
            // 이전 설정 메서드의 반환값인 HttpSecurity 객체를 반환하여, 다음 설정 메서드에서 이전 설정을 참조하도록 합니다.
            .and()
            // 이 기능은 CSRF 공격을 방지하기 위해 CSRF 토큰을 사용하여 요청을 검증합니다.
            .csrf().disable()
            // 토큰을 활용하는 경우 모든 요청에 대해 '인가'에 대해서 적용
//            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())

            // [STEP3] Spring Security JWT Filter Load
//            .addFilterBefore(jwtAuthorizationFilter(), BasicAuthenticationFilter.class)
            // session stateless로 설정하여 Rest API에 적합한 상태 없는 인증을 적용합니다.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
        // .form 기반의 로그인에 대해 비 활성화하며 커스텀으로 구성한 필터를 사용한다.
            .formLogin().disable()
        // authorizeRequests() 메서드는 antMatchers() 메서드와 함께 사용하여, 다양한 URL 경로나 리소스에 대한 접근 권한을 설정할 수 있습니다.
//            .authorizeRequests()
            .oauth2Login()				// OAuth2기반의 로그인인 경우
//                .loginPage("/loginForm");		// 인증이 필요한 URL에 접근하면 /loginForm으로 이동
                // 인가 엔드포인트를 설정
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .and()
                // 인가 후 redirect_uri 엔드 포인트 설정, 등록한 redirect_uri로 설정해야 함
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()			// 로그인 성공 후 사용자정보를 가져온다
                .userService(customOAuth2UserService)	//사용자정보를 처리할 때 사용한다
                .and()
                .successHandler(customAuthSuccessHandler);
//                .failureHandler()


        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            // AuthenticationConfiguration은 Spring Security에서 제공하는 인증 구성을 설정하는 인터페이스
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // AuthenticationConfiguration에서 설정한 인증 구성을 바탕으로 AuthenticationManager를 생성
        // UserSecurityService와 PasswordEncoder가 자동으로 설정
        return authenticationConfiguration.getAuthenticationManager();
    }


}
