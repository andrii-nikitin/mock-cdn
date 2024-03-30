package com.andriidnikitin.tools.mockcdn.controller;

import com.andriidnikitin.tools.mockcdn.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("/content")
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

  @GetMapping(value = "/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
  // TODO add actual type of downloaded file
  public @ResponseBody byte[] serveFile(@PathVariable String filename) throws IOException {
    Resource file = storageService.loadAsResource(filename);
    InputStream in = file.getInputStream();
    return IOUtils.toByteArray(in);
  }

  @PostMapping("/single/upload")
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
