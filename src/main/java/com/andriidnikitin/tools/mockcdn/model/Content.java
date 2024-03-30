package com.andriidnikitin.tools.mockcdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Data
@AllArgsConstructor
public class Content {
  private MediaType type;
  private Resource resource;
}
