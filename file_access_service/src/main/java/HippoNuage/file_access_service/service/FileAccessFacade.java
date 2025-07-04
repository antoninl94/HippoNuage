package HippoNuage.file_access_service.service;
import java.util.List;

import org.springframework.http.ResponseEntity;

import software.amazon.awssdk.services.s3.model.S3Object;


public interface FileAccessFacade {
    public ResponseEntity<List<S3Object>>accessFiles(String JwtToken);
    public ResponseEntity<?>getUserFile(String token, String fileNamen, boolean preview);
    public ResponseEntity<?>deleteObject(String jwt, String fileName);
    public ResponseEntity<?>renameObject(String jwt, String fileName, String newName);
}
