package com.andriidnikitin.tools.mockcdn.mvc;

import com.andriidnikitin.tools.mockcdn.exception.StorageFileNotFoundException;
import com.andriidnikitin.tools.mockcdn.service.StorageService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("!functionaltest")
public class UploadFileController {

  private final StorageService storageService;

  @Autowired
  public UploadFileController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/")
  public String listUploadedFiles(Model model) throws IOException {
    List<String> files = storageService.loadAll().collect(Collectors.toList());
    model.addAttribute("files", files);
    return "uploadForm";
  }

  @PostMapping("/")
  public String handleFileUpload(
      @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    String path = storageService.store(file);
    redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + path + "!");

    return "redirect:/";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
