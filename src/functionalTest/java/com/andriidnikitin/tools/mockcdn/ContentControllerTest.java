package com.andriidnikitin.tools.mockcdn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ContentControllerTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void smoke() {
    assertEquals(0, loadAllContent().size());
  }

  @Test
  void uploadImage() {
    assertEquals(0, loadAllContent().size());
    uploadContent("test/img.png").expectStatus().isOk();
    ;
    assertEquals(1, loadAllContent().size());
  }

  @Test
  void uploadText_notSupported() {
    uploadContent("test/text.txt").expectStatus().isBadRequest();
  }

  private List loadAllContent() {
    return webTestClient
        .get()
        .uri("/content")
        .exchange()
        .expectBody(List.class)
        .returnResult()
        .getResponseBody();
  }

  private WebTestClient.ResponseSpec uploadContent(String filename) {
    MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
    multipartBodyBuilder
        .part("file", new ClassPathResource(filename))
        .contentType(MediaType.MULTIPART_FORM_DATA);

    return webTestClient
        .post()
        .uri("/content")
        .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
        .exchange();
  }
}
