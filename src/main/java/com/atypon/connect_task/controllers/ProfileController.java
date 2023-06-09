package com.atypon.connect_task.controllers;

import com.atypon.connect_task.*;
import jakarta.servlet.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

  @GetMapping
  public String getProfile(HttpServletRequest request, @RequestHeader("Host") String host) {
    String scheme = request.getScheme();
    return UrlBuilder.builder().scheme(scheme).host(host).path("/api/profile").build();
  }
}