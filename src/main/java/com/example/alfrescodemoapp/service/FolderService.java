package com.example.alfrescodemoapp.service;

import com.example.alfrescodemoapp.dto.*;
import com.example.alfrescodemoapp.entity.FolderEntity;
import com.example.alfrescodemoapp.repository.DocumentRepository;
import com.example.alfrescodemoapp.repository.FolderRepository;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.alfrescodemoapp.service.AlfrescoFolderService.documentsId;
import static com.example.alfrescodemoapp.service.AlfrescoFolderService.foldersId;

@Service
public class FolderService {

    private final FolderRepository repository;
    private final AlfrescoFolderService alfrescoFolderService;

    private final DocumentRepository documentRepository;

    @Autowired
    public FolderService(FolderRepository repository, AlfrescoFolderService alfrescoFolderService, DocumentRepository documentRepository) {
        this.repository = repository;
        this.alfrescoFolderService = alfrescoFolderService;
        this.documentRepository = documentRepository;
    }

    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> getAll() {
        List<FolderDTO> folders = repository.getAll();

        return folders.isEmpty() ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK) : new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), folders), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse<FolderDTO, ErrorDTO>> get(String folderId) {
        FolderDTO folderDTO = repository.getByFolderId(folderId);

        return Objects.isNull(folderDTO) ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND) : new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), folderDTO), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> create(FolderCreateDTO dto) {

        ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> responseEntity = new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                new ErrorDTO("Already Exist")),
                HttpStatus.INTERNAL_SERVER_ERROR);

        if (dto.getParentFolderId().equals("0")) {
            String folderId = alfrescoFolderService.createFolder(dto.getName());

            repository.save(new FolderEntity(dto.getName(), dto.getParentFolderId(), folderId));
            responseEntity = new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK);
        } else if (repository.findByFolderId(dto.getParentFolderId()).isPresent()) {

            Optional<FolderEntity> optionalFolder = repository.findByParentIdAndAndName(dto.getParentFolderId(), dto.getName());

            if (optionalFolder.isEmpty()) {

                String folderId = alfrescoFolderService.createFolder(alfrescoFolderService.getFolderById(dto.getParentFolderId()), dto.getName());

                repository.save(new FolderEntity(dto.getName(), dto.getParentFolderId(), folderId));
                responseEntity = new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK);
            }
        }

        return responseEntity;
    }


    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> update(FolderUpdateDTO dto) {

        ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> responseEntity = new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                null,
                new ErrorDTO("Not Found")),
                HttpStatus.NOT_FOUND);


        Optional<FolderEntity> folder = repository.findByFolderId(dto.getFolderId());
        if (folder.isPresent()) {
            FolderEntity folderEntity = folder.get();

            if (!folderEntity.getName().equals(dto.getName())) {

                if (repository.findByParentIdAndAndName(folderEntity.getParentId(), dto.getName()).isEmpty()) {

                    alfrescoFolderService.update(dto.getFolderId(), dto.getName());
                    folderEntity.setName(dto.getName());

                    repository.save(folderEntity);

                    responseEntity = new ResponseEntity<>(new ApiResponse<>(
                            HttpStatus.OK.value()),
                            HttpStatus.OK);
                }
            }
        }
        return responseEntity;
    }


    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> delete(String folderId) {

        ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> responseEntity = new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                null,
                new ErrorDTO("Not Found")),
                HttpStatus.NOT_FOUND);

        Optional<FolderEntity> byFolderId = repository.findByFolderId(folderId);
        if (byFolderId.isPresent()) {

            Folder folderById = alfrescoFolderService.getFolderById(folderId);


            alfrescoFolderService.getAllChildIds(folderById);

            System.out.println("foldersId = " + foldersId);
            System.out.println("documentsId = " + documentsId);


            documentRepository.deleteAllByDocumentIdIn(documentsId);

            repository.deleteAllByFolderIdIn(foldersId);

            alfrescoFolderService.delete(folderById);

            responseEntity = new ResponseEntity<>(new ApiResponse<>(
                    HttpStatus.NO_CONTENT.value()),
                    HttpStatus.NO_CONTENT);
        }
        return responseEntity;
    }


    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> getSubFolders(String folderId) {

        List<FolderDTO> folders = repository.findAllByParentId(folderId);

        return folders.isEmpty() ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK) : new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), folders), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<FolderDTO>, ErrorDTO>> getMainFolders() {
        List<FolderDTO> folders = repository.getMainFolders();

        return folders.isEmpty() ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK) : new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), folders), HttpStatus.OK);
    }
}
