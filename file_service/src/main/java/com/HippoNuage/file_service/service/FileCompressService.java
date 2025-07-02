package com.HippoNuage.file_service.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static byte[] compressTxtCsv(MultipartFile file, Path destination) throws IOException {
            try (
        InputStream in = file.getInputStream();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteStream)
    ) {
        byte[] buffer = new byte[8192];
        int length;
        while ((length = in.read(buffer)) > -1) {
            gzipOut.write(buffer, 0, length);
        }
        // termine proprement la compression
        gzipOut.finish(); 
        return byteStream.toByteArray();
    }
    }

    // Méthode pour compresser les fichier jpg/jpeg
    public static byte[] compressImage(MultipartFile file, Path destination) throws IOException {
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

        //Preparation d'un flux de sortie en mémoire
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        
        //On cherche un ImageWriter et on gère le cas ou aucun n'est trouvé
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
        ios.close();

        byte[] compressedImageBytes = baos.toByteArray();
        baos.close();
        System.out.println("Fichier compressé");
        return compressedImageBytes;
        }
    

    public static byte[] compressPDF(MultipartFile multipartFile, Path destination, float quality) throws IOException {
        String extension = FilenameUtils.getExtension(destination.getFileName().toString().toLowerCase());
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

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();
            byte [] finalPDF = outputStream.toByteArray();
            return finalPDF;
            
        }
    }

    // Compresse les fichiers mp3 en passant par des lignes de commandes
    public static byte[] compressMp3(MultipartFile multipartFile, Path destination) throws IOException, InputFormatException, EncoderException, InterruptedException {
        // Crée un fichier temporaire
        Path tempInput = Files.createTempFile("input", ".mp3");
    multipartFile.transferTo(tempInput.toFile());

    // Utilise le nom de destination pour la logique, mais pas pour l’écriture disque
    String logicalFileName = destination.getFileName().toString();

    ProcessBuilder builder = new ProcessBuilder(
        "ffmpeg",
        "-i", tempInput.toString(),
        "-b:a", "96k",
        "-f", "mp3", // format de sortie mp3
        "-"          // "-" = sortie sur stdout
    );
    builder.redirectErrorStream(true);

    Process process = builder.start();

    try (InputStream is = process.getInputStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

        byte[] buffer = new byte[8192];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("ffmpeg process failed with exit code " + exitCode);
        }

        return baos.toByteArray();
    } finally {
        Files.deleteIfExists(tempInput);
    }
}
    

    // Compresse les fichiers mp4 en passant par des lignes de commandes
    public static byte[] compressMp4(MultipartFile multipartFile, Path destination) throws IOException, InputFormatException, EncoderException, InterruptedException {
        // Crée un fichier temporaire
        Path tempInput = Files.createTempFile("input", ".mp4");
        multipartFile.transferTo(tempInput.toFile());
         String logicalFileName = destination.getFileName().toString();

        // Crée un fichier temporaire pour l'output compressé
        Path tempOutput = Files.createTempFile("output", ".mp4");

        // Envoie la ligne de commande permettant la compression du fichier
        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg", "-y", // overwrite output
            "-i", tempInput.toString(),
            "-vcodec", "libx264",
            "-crf", "28",
            "-preset", "fast",
            "-acodec", "aac",
            "-b:a", "96k",
            "-movflags", "faststart", // important pour compatibilité lecteur
            tempOutput.toString()
        );
        builder.redirectErrorStream(true);

        Process process = builder.start();
             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[ffmpeg] " + line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("ffmpeg process failed with exit code " + exitCode);
            }
            byte[] compressedVideo = Files.readAllBytes(tempOutput);
            Files.deleteIfExists(tempInput);
            Files.deleteIfExists(tempOutput);

            return compressedVideo;
    }
}
