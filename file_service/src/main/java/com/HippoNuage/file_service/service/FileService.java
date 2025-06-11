package com.HippoNuage.file_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.model.File;
import com.HippoNuage.file_service.repository.FileRepository;

@Service
public class FileService {

    private final FileRepository fileRepository;

    private final Path rootLocation = Paths.get("upload");

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File uploadFile(UploadDto uploadDto, UUID userId) throws IOException {
        MultipartFile multipartFile = uploadDto.getFile();

        // Crée le dossier utilisateur s'il n'existe pas
        Path userFolder = rootLocation.resolve(userId.toString());
        Files.createDirectories(userFolder);

        Path destinationFile = userFolder.resolve(Paths.get(uploadDto.getName()))
                .normalize()
                .toAbsolutePath();

        multipartFile.transferTo(destinationFile);

        File fileEntity = new File();
        fileEntity.setName(uploadDto.getName());
        fileEntity.setPath(destinationFile.toString());
        fileEntity.setSize(multipartFile.getSize());
        fileEntity.setFormat(multipartFile.getContentType());
        fileEntity.setUserId(userId);

        return fileRepository.save(fileEntity);
    }
}
