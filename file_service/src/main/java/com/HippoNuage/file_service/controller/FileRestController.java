package com.HippoNuage.file_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.service.FileFacade;

@RestController
@RequestMapping("/file")
public class FileRestController {

    private final FileFacade fileFacade;

    @Autowired
    public FileRestController(FileFacade FileFacade) {
        this.fileFacade = FileFacade;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@ModelAttribute UploadDto uploadDto) {
            return this.fileFacade.upload(uploadDto);
    }
}
