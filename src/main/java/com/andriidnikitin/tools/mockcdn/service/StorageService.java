package com.andriidnikitin.tools.mockcdn.service;

import com.andriidnikitin.tools.mockcdn.model.Content;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  void init();

  String store(MultipartFile file);

  Stream<String> loadAll();

  Path load(String filename);

  Content loadAsResource(String filename);

  void delete(String filename);

  void deleteAll();
}
