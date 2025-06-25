package com.HippoNuage.file_service.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;




@Service
public class FileCompressService {

    public static void compressTxtCsv(MultipartFile file, Path destination) throws IOException {
        try (
            InputStream in = file.getInputStream();
            OutputStream out = new GZIPOutputStream(Files.newOutputStream(destination));
        ) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
            }
        }
    }

    // Méthode pour compresser les fichier jpg/jpeg
    public static void compressImage(MultipartFile file, Path destination) throws IOException {
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

    public static void compressPDF(MultipartFile multipartFile, Path destination, float quality) throws IOException {
        try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
            document.setAllSecurityToBeRemoved(true);
            // Parcourt les pages du document
            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();
                // Parcourt les objets de la page
                for (COSName xObjectName : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(xObjectName);
                    // Si l'objet est une image : l'image est compressée
                    if (xObject instanceof PDImageXObject imageXObject) {
                        BufferedImage image = imageXObject.getImage();
                        if (image == null) {
                            System.out.println("Image non lisible, ignorée : " + xObjectName.getName());
                            continue;
                        }
                        if (imageXObject.getColorSpace().getName().equals("DeviceGray")
                                && imageXObject.getBitsPerComponent() <= 1) {
                            continue;
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                        ImageWriteParam param = writer.getDefaultWriteParam();
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(quality);

                        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                            writer.setOutput(ios);
                            writer.write(null, new IIOImage(image, null, null), param);
                        } finally {
                            writer.dispose();
                        }
                        PDImageXObject compressedImage = PDImageXObject.createFromByteArray(
                                document,
                                baos.toByteArray(),
                                xObjectName.getName()
                        );
                        resources.put(xObjectName, compressedImage);
                    }
                }
            }

            for (COSObject cosObject : document.getDocument().getObjects()) {
                COSBase base = cosObject.getObject();
                if (base instanceof COSStream stream) {
                    stream.setNeedToBeUpdated(true);
                }
            }

            document.save(destination.toFile());
        }
    }

    // Compresse les fichiers mp3 en passant par des lignes de commandes
    public static void compressMp3(MultipartFile multipartFile, Path destination) throws IOException, InputFormatException, EncoderException {
        // Crée un fichier temporaire
        Path tempInput = Files.createTempFile("input", ".mp3");
        multipartFile.transferTo(tempInput.toFile());

        // Envoie la ligne de commande permettant la compression du fichier
        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg", "-i", tempInput.toString(), "-b:a", "96k", destination.toString()
        );
        builder.inheritIO();
        Process process = builder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("ffmpeg process failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("ffmpeg process was interrupted", e);
        } finally {
            Files.deleteIfExists(tempInput);
        }
    }

    // Compresse les fichiers mp4 en passant par des lignes de commandes
    public static void compressMp4(MultipartFile multipartFile, Path destination) throws IOException, InputFormatException, EncoderException {
        // Crée un fichier temporaire
        Path tempInput = Files.createTempFile("input", ".mp4");
        multipartFile.transferTo(tempInput.toFile());

        // Envoie la ligne de commande permettant la compression du fichier
        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg", "-i", tempInput.toString(), "-vcodec", "libx264", "-crf", "28", "-preset", "fast", "-acodec", "aac", "-b:a", "96k", destination.toString()
        );
        builder.inheritIO();
        Process process = builder.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("ffmpeg process failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("ffmpeg process was interrupted", e);
        } finally {
            Files.deleteIfExists(tempInput);
        }
    }
}
