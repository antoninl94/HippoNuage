package com.HippoNuage.file_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HippoNuage.file_service.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
}
