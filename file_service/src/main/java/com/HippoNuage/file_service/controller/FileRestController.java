package com.HippoNuage.file_service.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<File> multiUpload(@RequestParam("file") List<MultipartFile> files, @AuthenticationPrincipal Jwt jwt) throws IOException {
        UUID userId = jwtService.extractUserId(jwt);
        List<File> uploadedFiles = new ArrayList<>();

        for (MultipartFile file: files) {
            UploadDto uploadDto = new UploadDto();
            uploadDto.setName(file.getOriginalFilename());
            uploadDto.setFile(file);
            uploadedFiles.add(fileFacade.uploadFile(uploadDto, userId));
        }

        return uploadedFiles;
    }
}
