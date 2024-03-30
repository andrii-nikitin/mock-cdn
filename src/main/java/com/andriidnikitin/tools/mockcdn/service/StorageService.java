package com.andriidnikitin.tools.mockcdn.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

  void init();

  String store(MultipartFile file);

  Stream<String> loadAll();

  Path load(String filename);

  Resource loadAsResource(String filename);

  void delete(String filename);

  void deleteAll();
}
