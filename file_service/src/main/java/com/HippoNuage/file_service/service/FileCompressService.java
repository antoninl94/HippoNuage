package com.HippoNuage.file_service.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileCompressService {

    public void compressImage(MultipartFile file, Path destination) throws IOException {
        // Récupère l'image
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
          throw new IllegalArgumentException("Fichier image non lisible ou format non supporté.");
        }

        // Récupère l'extension de l'image
        String extension = FilenameUtils.getExtension(destination.getFileName().toString().toLowerCase());

        // Vérifie que l'image/fichier est bien au format jpg ou jpeg
        List<String> allowedFormats = List.of("jpg", "jpeg");
        if (!allowedFormats.contains(extension)) {
            throw new IllegalArgumentException("Format non supporté : " + extension);
        }

        try (OutputStream os = Files.newOutputStream(destination); ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);
            if (!writers.hasNext()) {
                throw new IllegalStateException("No jpg/jpeg writers found");
            }

            ImageWriter writer = writers.next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.3f);

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }
    }

    public void compressPDF(MultipartFile multipartFile, Path destination) {
      
    }
}
