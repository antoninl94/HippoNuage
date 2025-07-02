package HippoNuage.file_access_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import HippoNuage.file_access_service.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
}
