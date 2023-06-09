package com.atypon.connect_task.config;

import org.springframework.boot.web.servlet.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").permitAll()
        )
        .httpBasic(AbstractHttpConfigurer::disable)
        .build();
  }

    @Bean
    public FilterRegistrationBean<ThrottlerFilter> throttlerFilter() {
      FilterRegistrationBean<ThrottlerFilter> bean = new FilterRegistrationBean<>();
      bean.setFilter(new ThrottlerFilter());
      bean.addUrlPatterns("/api/*");
      return bean;
    }
}
