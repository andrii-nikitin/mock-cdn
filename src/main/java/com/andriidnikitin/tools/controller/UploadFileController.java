package com.andriidnikitin.tools.controller;

import com.andriidnikitin.tools.exception.StorageFileNotFoundException;
import com.andriidnikitin.tools.service.StorageService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class UploadFileController {

  private final StorageService storageService;

  @Autowired
  public UploadFileController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/")
  public String listUploadedFiles(Model model) throws IOException {
    List<String> files =
        storageService.loadAll().map(UploadFileController::toFilePath).collect(Collectors.toList());
    model.addAttribute("files", files);
    return "uploadForm";
  }

  private static String toFilePath(Path path) {
    String fileName = path.getFileName().toString();
    UriComponentsBuilder fileUri =
        MvcUriComponentsBuilder.fromMethodName(UploadFileController.class, "serveFile", fileName);
    return fileUri.build().toUri().toString();
  }

  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

    Resource file = storageService.loadAsResource(filename);

    if (file == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @PostMapping("/")
  public String handleFileUpload(
      @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    storageService.store(file);
    redirectAttributes.addFlashAttribute(
        "message", "You successfully uploaded " + file.getOriginalFilename() + "!");

    return "redirect:/";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
