package com.andriidnikitin.tools.mockcdn.controller;

import com.andriidnikitin.tools.mockcdn.model.Content;
import com.andriidnikitin.tools.mockcdn.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/content")
public class ContentController {

  private final StorageService storageService;

  @Autowired
  public ContentController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping
  public List<String> listAll() {
    return storageService.loadAll().collect(Collectors.toList());
  }

  @GetMapping(
      value = "/{filename:.+}",
      produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
  public ResponseEntity<byte[]> serveFile(@PathVariable String filename) throws IOException {
    Content content = storageService.loadAsResource(filename);
    InputStream in = content.getResource().getInputStream();
    byte[] result = IOUtils.toByteArray(in);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(content.getType())
        .body(result);
  }

  @PostMapping
  public ResponseEntity<String> fileUploading(@RequestParam("file") MultipartFile file) {
    // TODO validate type of uploaded file
    String filename = storageService.store(file);
    return ResponseEntity.ok(filename);
  }

  @DeleteMapping(value = "/{filename:.+}")
  void deleteEmployee(@PathVariable String filename) {
    storageService.delete(filename);
  }
}
