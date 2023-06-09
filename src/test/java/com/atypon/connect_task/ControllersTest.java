package com.atypon.connect_task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.web.context.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ControllersTest {
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @ParameterizedTest
  @MethodSource("controllerUrls")
  void testControllers(String path, String expectedUrl) throws Exception {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getScheme()).thenReturn("http");
    String host = "example.com";

    mockMvc.perform(get(path)
            .header("Host", host))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedUrl));
  }

  private static Stream<Arguments> controllerUrls() {
    return Stream.of(
        Arguments.of("/api/history", "http://example.com/api/history"),
        Arguments.of("/api/posts", "http://example.com/api/posts"),
        Arguments.of("/api/profile", "http://example.com/api/profile")
    );
  }
}
