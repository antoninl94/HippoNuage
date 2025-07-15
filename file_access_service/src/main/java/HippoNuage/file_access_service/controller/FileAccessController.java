package HippoNuage.file_access_service.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import HippoNuage.file_access_service.config.JWTConfig;
import HippoNuage.file_access_service.dto.FileDto;
import HippoNuage.file_access_service.service.FileAccessFacade;
import software.amazon.awssdk.services.s3.model.S3Object;


@RestController
@RequestMapping("/file_access")
@Validated
public class FileAccessController {

    private final FileAccessFacade fileFacade;
    private final JWTConfig jwtConfig;

    @Autowired
    public FileAccessController(FileAccessFacade FileAccessFacade, JWTConfig jwtConfig) {
        this.fileFacade = FileAccessFacade;
        this.jwtConfig = jwtConfig;
    }
    //Retrieve a list of files belonging to a user (based on jwt.user_id)
    @GetMapping("/getFilesByUser")
    public ResponseEntity<?>accessFiles(@RequestHeader("Authorization") String JwtToken) throws Exception{
        System.out.println("Bonjour je commence la fonction");
        String token = null;
        if (JwtToken != null && JwtToken.startsWith("Bearer ")) {
            token = JwtToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai trouvé le token");
        // Validation & extraction userId du JWT
        String userId = jwtConfig.extractUserId(token);
        if (userId == null || jwtConfig.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai validé l'user avec le token");
        // Appel au service métier pour récupérer les fichiers
        ResponseEntity<List<S3Object>> S3Response = fileFacade.accessFiles(token);
        List<S3Object> S3Objects = S3Response.getBody();
        List<FileDto> files = S3Objects.stream()
        .map(s3 -> new FileDto(
            extractFileName(s3.key()),
            s3.size(),
            s3.lastModified()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(files);
    }
   
    //Testing purposes
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("Ping reçu !");
        return ResponseEntity.ok("pong");
    }

    //Retrieve a file fpr preview or download, depending on preview parameter
    @GetMapping("/getFile")
    public ResponseEntity<?>getUserFile(@RequestHeader("Authorization") String token, @RequestParam("filename") String filename, @RequestParam(value = "preview", defaultValue="false") boolean preview){
        System.out.println("Bonjour je commence la fonction");
        String jwt = null;
        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai trouvé le token");
        // Validation & extraction userId from JWT
        String userId = jwtConfig.extractUserId(jwt);
        if (userId == null || jwtConfig.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai validé l'user avec le token");
        return fileFacade.getUserFile(jwt, filename, preview);
        
    }
    // Delete a file 
    @DeleteMapping("/delete")
    public ResponseEntity<?>deleteObject(@RequestHeader("Authorization") String token, @RequestParam("filename") String fileName) {
        String jwt = null;
        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai trouvé le token");
        // Validation & extraction userId du JWT
        String userId = jwtConfig.extractUserId(jwt);
        if (userId == null || jwtConfig.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return fileFacade.deleteObject(jwt, fileName);
    }

    // rename a file
    @PutMapping("/rename")
    public ResponseEntity<?>renameFile(@RequestHeader("Authorization") String token, @RequestParam("fileName") String fileName, @RequestParam("newName") String newName){
        String jwt = null;
        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("J'ai trouvé le token");
        // Validation & extraction userId from JWT
        String userId = jwtConfig.extractUserId(jwt);
        if (userId == null || jwtConfig.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
                return this.fileFacade.renameObject(jwt, fileName, newName);
        }   catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
}


    //Outil pour retrieve un name lors de l'appel vers la liste s3 d'un utilisateur
    private String extractFileName(String key) {
        if (key == null || !key.contains("/")) {
            return key;
        }
        return key.substring(key.lastIndexOf("/") + 1);
    }


}
