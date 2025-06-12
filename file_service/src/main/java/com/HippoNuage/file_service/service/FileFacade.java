package com.HippoNuage.file_service.service;

import java.io.IOException;
import java.util.UUID;

import com.HippoNuage.file_service.dto.UploadDto;
import com.HippoNuage.file_service.model.File;

public interface FileFacade {

    public File uploadFile(UploadDto uploadDto, UUID userId) throws IOException;
}
