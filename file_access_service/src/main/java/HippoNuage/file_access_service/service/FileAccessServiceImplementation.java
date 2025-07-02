package HippoNuage.file_access_service.service;

import java.io.BufferedInputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import HippoNuage.file_access_service.config.JWTConfig;
import HippoNuage.file_access_service.repository.FileRepository;
import HippoNuage.file_access_service.tools.AccessTools;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class FileAccessServiceImplementation implements FileAccessFacade{

    @Value("${aws.bucket}")
    private String bucketName;

    private final FileRepository fileRepository;
    private final JWTConfig jwtConfig;
    private final S3Client S3Client;
    
    @Autowired
    public FileAccessServiceImplementation(FileRepository fileRepository, JWTConfig jwtConfig, S3Client s3Client) {
        this.fileRepository = fileRepository;
        this.jwtConfig = jwtConfig;
        this.S3Client = s3Client;
    }


    @Override
    public ResponseEntity<List<S3Object>>accessFiles(String JwtToken) {
       
        String userId = this.jwtConfig.extractUserId(JwtToken);
        boolean isExpired;
        try {
        isExpired = jwtConfig.isTokenExpired(JwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userId == null || isExpired) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String prefix = userId + "/";
         List<S3Object> userFiles;
        try {
        userFiles = this.S3Client.listObjectsV2(ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix(prefix)
        .build())
        .contents();
        }
        catch (S3Exception e) {
            userFiles = Collections.EMPTY_LIST;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userFiles);
        }
        return ResponseEntity.ok(userFiles);
    }

    @Override
    public ResponseEntity<?>getUserFile(String jwt, String fileName, boolean preview) {
        String userId = this.jwtConfig.extractUserId(jwt);
        boolean isExpired;
        String prefix = userId + "/" + fileName;
        try {
        isExpired = jwtConfig.isTokenExpired(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userId == null || isExpired) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            
        try {
        // Préparer la requête pour récupérer l'objet S3
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(prefix)
            .build();

        ResponseInputStream<GetObjectResponse> s3ObjectStream = S3Client.getObject(getObjectRequest);
        try (BufferedInputStream bufferedStream = new BufferedInputStream(s3ObjectStream)){
            boolean isGzipped = AccessTools.isGzipped(bufferedStream);
            byte[] content;
            String outFileName = fileName;
            String contentType = s3ObjectStream.response().contentType();

            if (isGzipped) {
                // 3. Décompresser à la volée
                System.out.println("Je décompresse");
                content = AccessTools.decompressGzip(bufferedStream);
                // Adapter le nom du fichier (enlever .gz si présent)
                if (fileName.endsWith(".gz")) {
                    outFileName = fileName.substring(0, fileName.length() - 3);
                }
                // Adapter le content-type si besoin
                if ("application/gzip".equals(contentType)) {
                    contentType = "application/octet-stream";
                }
            } else {
                // 4. Lire normalement
                content = bufferedStream.readAllBytes();
            }

            // Construire les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            // Sécurise le contentType
            if (contentType == null || !contentType.contains("/")) {
                if (outFileName.endsWith(".csv")) {
                    contentType = "text/csv";
                } else if (outFileName.endsWith(".json")) {
                    contentType = "application/json";
                } else if (outFileName.endsWith(".txt")) {
                    contentType = "text/plain";
                } else if (outFileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            headers.setContentType(MediaType.parseMediaType(contentType));
            if (preview) {
                // Affichage inline dans le navigateur
                headers.setContentDisposition(ContentDisposition.inline().filename(outFileName).build());
            } else {
                // Forcer le téléchargement
                headers.setContentDisposition(ContentDisposition.attachment().filename(outFileName).build());
            }

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        }
    } catch (NoSuchKeyException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé");
    } catch (Exception e) {
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération du fichier");
    }
    }
}