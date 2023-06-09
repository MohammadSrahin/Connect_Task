package com.atypon.connect_task.config;

import com.atypon.connect_task.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import org.springframework.web.filter.*;

public class ThrottlerFilter extends OncePerRequestFilter {
  private final Throttler throttler = new Throttler();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String clientIp = request.getRemoteAddr();
    String requestPath = request.getRequestURI();

    if (throttler.canAccess(clientIp, requestPath)) {
      chain.doFilter(request, response);
    } else {
      response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
  }
}
