package HippoNuage.file_access_service.dto;
import java.time.Instant;

public class FileDto {
    private String name;
    private Long size;
    private Instant uploadedAt;

    public FileDto(String name, Long size, Instant uploadedAt) {
        this.name = name;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}