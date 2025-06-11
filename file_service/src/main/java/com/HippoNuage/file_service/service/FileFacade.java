package com.HippoNuage.file_service.service;

import org.springframework.http.ResponseEntity;

import com.HippoNuage.file_service.dto.UploadDto;

public interface FileFacade {
  public ResponseEntity<?> upload(UploadDto uploadDto);
}
