package com.HippoNuage.file_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.model.File;
import com.HippoNuage.file_service.repository.FileRepository;

@Service
public class FileServiceImplementation implements FileFacade {

    private final FileRepository fileRepository;

    private final ValidateFileService validateFileService;

    private final Path rootLocation = Paths.get("upload");

    public FileServiceImplementation(FileRepository fileRepository, ValidateFileService validateFileService) {
        this.fileRepository = fileRepository;
        this.validateFileService = validateFileService;
    }

    @Override
    public File uploadFile(UploadDto uploadDto, UUID userId) throws IOException {
        MultipartFile multipartFile = uploadDto.getFile();
        System.out.println("Début upload : " + multipartFile.getOriginalFilename());

        // Vérifie la validité du fichier et fixe une limite de taille en fonction
        validateFileService.validateFile(multipartFile);
        System.out.println("validation ok : " + multipartFile.getOriginalFilename());

        // Crée le dossier utilisateur s'il n'existe pas
        Path userFolder = rootLocation.resolve(userId.toString());
        Files.createDirectories(userFolder);

        String originalFileName = uploadDto.getName();

        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);

        // Défini le chemin du fichier
        Path destinationFile = userFolder.resolve(originalFileName)
                .normalize()
                .toAbsolutePath();

        // Renomme le fichier en cas de doublon
        int count = 1;
        while (Files.exists(destinationFile)) {
            String newFileName = baseName + "(" + count + ")";
            if (!extension.isEmpty()) {
                newFileName += "." + extension;
            }
            destinationFile = userFolder.resolve(newFileName).normalize().toAbsolutePath();
            count += 1;
        }

        try {
            // Vérifie le type de fichier si c'est un jpg/jpeg alors le fichier est compressé
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                FileCompressService.compressImage(multipartFile, destinationFile);
            } else if (extension.equalsIgnoreCase("pdf")) {
                FileCompressService.compressPDF(multipartFile, destinationFile, 0.3f);
            } else if (extension.equalsIgnoreCase("mp3")) {
                FileCompressService.compressMp3(multipartFile, destinationFile, 64000);
            } else {
                multipartFile.transferTo(destinationFile);
            }

            System.out.println("Fichier transféré : " + destinationFile.toString());

            File fileEntity = new File();
            fileEntity.setName(destinationFile.getFileName().toString());
            fileEntity.setPath(destinationFile.toString());
            fileEntity.setSize(Files.size(destinationFile));
            fileEntity.setFormat(multipartFile.getContentType());
            fileEntity.setUserId(userId);

            return fileRepository.save(fileEntity);

        } catch (Exception e) {
            try {
                // En cas d'échec, suprime le fichier de la bdd
                if (Files.exists(destinationFile)) {
                    Files.delete(destinationFile);
                }
            } catch (IOException cleanuException) {
                System.err.println("Erreur de nettoyage : " + cleanuException.getMessage());
            }

            System.err.println("Erreur lors du traitement du fichier : " + e.getMessage());
            throw new IllegalArgumentException("Échec du traitement du fichier : " + multipartFile.getOriginalFilename(), e);
        }
    }
}
