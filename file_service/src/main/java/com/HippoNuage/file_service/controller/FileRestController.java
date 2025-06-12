package com.HippoNuage.file_service.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.model.File;
import com.HippoNuage.file_service.service.FileFacade;
import com.HippoNuage.file_service.service.JwtService;

@RestController
@RequestMapping("/file")
@Validated
public class FileRestController {

    private final FileFacade fileFacade;
    private final JwtService jwtService;

    @Autowired
    public FileRestController(FileFacade FileFacade, JwtService jwtService) {
        this.fileFacade = FileFacade;
        this.jwtService = jwtService;
    }

    @PostMapping("/upload")
    public File upload(@ModelAttribute UploadDto uploadDto, @AuthenticationPrincipal Jwt jwt) throws IOException {
        UUID userId = jwtService.extractUserId(jwt);
        return fileFacade.uploadFile(uploadDto, userId);
    }
}
