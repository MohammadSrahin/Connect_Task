package com.atypon.connect_task.controllers;

import com.atypon.connect_task.*;
import jakarta.servlet.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

  @GetMapping
  public String getPosts(HttpServletRequest request, @RequestHeader("Host") String host) {
    String scheme = request.getScheme();
    return UrlBuilder.builder().scheme(scheme).host(host).path("/api/posts").build();
  }
}
