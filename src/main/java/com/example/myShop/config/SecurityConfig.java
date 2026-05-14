package com.example.myShop.config;

import com.example.myShop.constant.Role;
import com.example.myShop.dto.security.ShopPrinciple;
import com.example.myShop.jwt.JwtAuthenticationFilter;
import com.example.myShop.jwt.JwtTokenProvider;
import com.example.myShop.repository.MemberRepository;
import com.example.myShop.service.RefreshTokenService;
import com.example.myShop.service.oauth2.CustomOAuth2UserService;
import com.example.myShop.service.oauth2.OAuth2SuccessHandler;
import com.example.myShop.service.security.JwtLogoutHandler;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2SuccessHandler oAuth2SuccessHandler,
      JwtLogoutHandler jwtLogoutHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    return http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .antMatchers(
                        "/",
                        "/viewtest/**",
                        "/members/**",
                        "/item/**",
                        "/images/**",
                        "/oauth2/**",
                        "/login/oauth2/**")
                    .permitAll()
                    .antMatchers("/admin/**")
                    .hasRole(Role.ADMIN.name())
                    .anyRequest()
                    .authenticated())
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/members/login")
                    .successHandler(oAuth2SuccessHandler)
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
        .logout(
            logout ->
                logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .addLogoutHandler(jwtLogoutHandler))
        .exceptionHandling(
            config ->
                config.authenticationEntryPoint(
                    (request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
    return new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenService);
  }

  @Bean
  public UserDetailsService userDetailsService(MemberRepository memberRepository) {
    return username ->
        Optional.ofNullable(memberRepository.findByEmail(username))
            .map(ShopPrinciple::from)
            .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
