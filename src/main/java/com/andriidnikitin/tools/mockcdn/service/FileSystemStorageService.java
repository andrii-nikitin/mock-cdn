package com.andriidnikitin.tools.mockcdn.service;

import com.andriidnikitin.tools.mockcdn.controller.ContentController;
import com.andriidnikitin.tools.mockcdn.exception.StorageException;
import com.andriidnikitin.tools.mockcdn.exception.StorageFileNotFoundException;
import com.andriidnikitin.tools.mockcdn.model.Content;
import com.andriidnikitin.tools.mockcdn.mvc.UploadFileController;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;

  @Autowired
  public FileSystemStorageService(@Value("${app.upload-dir}") String folder) {
    if (folder.trim().length() == 0) {
      throw new StorageException("File upload location can not be Empty.");
    }
    this.rootLocation = Paths.get(folder);
  }

  @Override
  public String store(MultipartFile file) {
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file.");
      }
      String originalFilename = file.getOriginalFilename();
      String fileExtension = FilenameUtils.getExtension(originalFilename);
      Path resultPath = buildNewFilePath(fileExtension);
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, resultPath, StandardCopyOption.REPLACE_EXISTING);
        return resultPath.getFileName().toString();
      }
    } catch (IOException e) {
      throw new StorageException("Failed to store file.", e);
    }
  }

  @Override
  public Stream<String> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
          .filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize)
          .map(FileSystemStorageService::toFilePath);
    } catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Content loadAsResource(String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        String fileExtension = FilenameUtils.getExtension(file.getFileName().toString());
        MediaType mediaType = MediaType.parseMediaType("image/" + fileExtension);
        return new Content(mediaType, resource);
      } else {
        throw new StorageFileNotFoundException("Could not read file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }

  @Override
  public void delete(String filename) {
    Path path = rootLocation.resolve(filename);
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  private static String toFilePath(Path path) {
    String fileName = path.getFileName().toString();
    UriComponentsBuilder fileUri =
        MvcUriComponentsBuilder.fromMethodName(ContentController.class, "serveFile", fileName);
    return fileUri.build().toUri().toString();
  }

  private Path buildNewFilePath(String fileExtension) {
    Path resultPath;
    final String tempFilename = UUID.randomUUID().toString();
    String fileName =
        Optional.ofNullable(fileExtension)
            .map(String::trim)
            .filter(x -> !x.isEmpty())
            .map(x -> tempFilename + "." + fileExtension)
            .orElse(tempFilename);
    Path destinationFile =
        this.rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
    if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
      // This is a security check
      throw new StorageException("Cannot store file outside current directory.");
    }
    resultPath = Files.exists(destinationFile) ? buildNewFilePath(fileExtension) : destinationFile;
    return resultPath;
  }
}
