package com.HippoNuage.file_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ValidateFileService {

    public void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        // Récupère la taille du fichier à uploader
        Long size = file.getSize();

        // Vérifie que le fichier n'est pas null
        if (contentType == null) {
            throw new IllegalArgumentException("Type de fichier non reconnu");
        }

        // Fixe une limite de taille selon le type de fichier et défini le type de fichier autorisé
        switch (contentType) {
            case "image/png":
            case "image/jpeg":
            case "image/jpg":
                if (size > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("Les images ne doivent pas dépasser 5 Mo");
                }
                break;
            case "application/pdf":
            case "text/plain":
            case "text/csv":
                if (size > 10 * 1024 * 1024) {
                  throw new IllegalArgumentException("Les PDF et txt ne doivent pas dépasser 10 Mo");
                }
                break;
            case "audio/mp3":
            case "audio/mpeg":
                if (size > 50 * 1024 * 1024) {
                  throw new IllegalArgumentException("Les fichiers MP3 ne peuvent pas dépasser 50 Mo");
                }
                break;
            case "video/mp4":
            case "video/quicktime":
                if (size > 100 * 1024 *1024) {
                  throw new IllegalArgumentException("Les vidéos ne doivent pas dépasser 100 Mo");
                }
                break;
            default:
                throw new IllegalArgumentException("Type de fichier non supporté : " + contentType);
        }
    }
}
