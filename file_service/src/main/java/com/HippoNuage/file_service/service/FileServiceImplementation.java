package com.HippoNuage.file_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.model.File;
import com.HippoNuage.file_service.repository.FileRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class FileServiceImplementation implements FileFacade {

    private final FileRepository fileRepository;

    private final ValidateFileService validateFileService;

    @Value("${aws.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    private final Path rootLocation = Paths.get("upload");

    public FileServiceImplementation(FileRepository fileRepository, ValidateFileService validateFileService, S3Client s3Client) {
        this.fileRepository = fileRepository;
        this.validateFileService = validateFileService;
        this.s3Client = s3Client;
    }

    @Override
    public File uploadFile(UploadDto uploadDto, UUID userId) throws IOException {
        MultipartFile multipartFile = uploadDto.getFile();
        System.out.println("Début upload : " + multipartFile.getOriginalFilename());

        // Vérifie la validité du fichier et fixe une limite de taille en fonction
        validateFileService.validateFile(multipartFile);
        System.out.println("validation ok : " + multipartFile.getOriginalFilename());

        String originalFileName = uploadDto.getName();

        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);

        // Crée la clé s3 liée à l'utilisateur
        String s3Key = userId + "/" + originalFileName;
        Path pathS3Key = Paths.get(s3Key);
        Path destinationFile = pathS3Key;

        // Renomme le fichier en cas de doublon
        int count = 1;
        while (doesObjectExists(bucketName, s3Key)) {
           String newFileName = baseName + "(" + count + ")";
            if (!extension.isEmpty()) {
                newFileName += "." + extension;
            }
            s3Key = userId + "/" + newFileName;
            count++;
        }
        System.out.println("path = " + s3Key);
        byte[] imageDataCpy = null;
        try {
            // Vérifie le type de fichier et applique la compression adaptée
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                byte[] imageData = FileCompressService.compressImage(multipartFile, destinationFile);
                imageDataCpy = imageData;
                PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key) 
                    .contentType(extension) 
                    .build();
                s3Client.putObject(request, RequestBody.fromBytes(imageData));
                

            } else if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("csv")){
                if (multipartFile.getSize() > 2000) {
                        byte[] txtCsvByteArray = FileCompressService.compressTxtCsv(multipartFile, destinationFile);
                        imageDataCpy = txtCsvByteArray;
                    PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key) 
                        .contentType(extension) 
                        .build();
                    s3Client.putObject(request, RequestBody.fromBytes(txtCsvByteArray));
                } else {
                    byte[] rawBytes = multipartFile.getBytes();
                    imageDataCpy = rawBytes;
                    PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key) 
                        .contentType(extension) 
                        .build();
                    s3Client.putObject(request, RequestBody.fromBytes(rawBytes)); 
                }
            } else if (extension.equalsIgnoreCase("pdf")) {
                byte [] pdfByteArray = FileCompressService.compressPDF(multipartFile, destinationFile, 0.3f);
                imageDataCpy = pdfByteArray;
                PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key) 
                    .contentType(extension) 
                    .build();
                s3Client.putObject(request, RequestBody.fromBytes(pdfByteArray));
            } else if (extension.equalsIgnoreCase("mp3")) {
                byte[] mp3ByteArray = FileCompressService.compressMp3(multipartFile, destinationFile);
                imageDataCpy = mp3ByteArray;
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key) 
                        .contentType("audio/mpeg") 
                        .build();
                s3Client.putObject(request, RequestBody.fromBytes(mp3ByteArray));
            } else if (extension.equalsIgnoreCase("mp4")) {
               byte[] mp4ByteArray = FileCompressService.compressMp4(multipartFile, destinationFile);
                imageDataCpy = mp4ByteArray;
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key) 
                        .contentType("video/mp4") 
                        .build();
                s3Client.putObject(request, RequestBody.fromBytes(mp4ByteArray));
            } else {
                byte[] fileBytes = multipartFile.getBytes();
                imageDataCpy = fileBytes;
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key) 
                        .contentType(extension) 
                        .build();
                s3Client.putObject(request, RequestBody.fromBytes(fileBytes));
            }

            System.out.println("Fichier transféré : " + destinationFile.toString());

            File fileEntity = new File();
            long fileSize = 0;
            if (Files.exists(destinationFile)) {
                fileSize = Files.size(destinationFile);
            } else {
                fileSize = imageDataCpy.length; // ou multipartFile.getSize() si tu as pas imageData
            }
            fileEntity.setName(destinationFile.getFileName().toString());
            fileEntity.setPath(destinationFile.toString());
            fileEntity.setSize(fileSize);
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
    
    private boolean doesObjectExists(String bucketName, String key) {
    try {
        s3Client.headObject(b -> b.bucket(bucketName).key(key));
        return true; // L’objet existe
    } catch (NoSuchKeyException e) {
        return false; // L’objet n’existe pas
    } catch (S3Exception e) {
        if (e.statusCode() == 404) {
            return false;
        }
        throw e; // autre erreur
    }
}
}
