package com.example.alfrescodemoapp.service;

import com.example.alfrescodemoapp.config.AlfrescoConfiguration;
import com.example.alfrescodemoapp.dto.*;
import com.example.alfrescodemoapp.entity.DocumentEntity;
import com.example.alfrescodemoapp.entity.FolderEntity;
import com.example.alfrescodemoapp.repository.DocumentRepository;
import com.example.alfrescodemoapp.repository.FolderRepository;
import lombok.SneakyThrows;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {


    private final DocumentRepository repository;
    private final AlfrescoDocumentService alfrescoDocumentService;

    private final FolderRepository folderRepository;

    private final AlfrescoFolderService alfrescoFolderService;
    private final AlfrescoConfiguration alfrescoConfiguration;

    @Autowired
    public DocumentService(DocumentRepository repository, AlfrescoDocumentService alfrescoDocumentService, FolderRepository folderRepository, AlfrescoFolderService alfrescoFolderService, AlfrescoConfiguration alfrescoConfiguration) {
        this.repository = repository;
        this.alfrescoDocumentService = alfrescoDocumentService;
        this.folderRepository = folderRepository;
        this.alfrescoFolderService = alfrescoFolderService;
        this.alfrescoConfiguration = alfrescoConfiguration;
    }

    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> getAll() {
        List<DocumentDTO> documents = repository.getAll();

        for (DocumentDTO document : documents) {
            List<String> versions = new ArrayList<>();

            Document documentById = alfrescoDocumentService.getDocumentById(document.getDocumentId(), null);

            List<Document> allVersions = documentById.getAllVersions();
            for (Document allVersion : allVersions) {
                versions.add(allVersion.getVersionLabel());
            }

            document.setVersions(versions);
        }

        return documents.isEmpty() ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK) :
                new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), documents), HttpStatus.OK);
    }

    @SneakyThrows
    public ResponseEntity download(String documentId, String version) {

        Optional<DocumentEntity> byDocumentId = repository.findByDocumentId(documentId);

        ResponseEntity respEntity = new ResponseEntity(HttpStatus.NOT_FOUND);


        if (byDocumentId.isPresent()) {

            DocumentEntity documentEntity = byDocumentId.get();
            Document document = alfrescoDocumentService.getDocumentById(documentEntity.getDocumentId(), version);
            ContentStream contentStream = document.getContentStream();

            InputStream inputStream = contentStream.getStream();

            byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=" +
                    document.getName());
            responseHeaders.add("Content-Type", document.getContentStreamMimeType());
            responseHeaders.add("file-name",document.getName());


            respEntity = new ResponseEntity(out, responseHeaders, HttpStatus.OK);
        }

        return respEntity;
    }

    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> create(DocumentCreateDTO dto) {
        ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> responseEntity = new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                new ErrorDTO("Already Exist")),
                HttpStatus.INTERNAL_SERVER_ERROR);

        Optional<FolderEntity> byFolderId = folderRepository.findByFolderId(dto.getFolderId());

        if (dto.getFolderId().equals("0")) {

            String documentId = alfrescoDocumentService.create(alfrescoConfiguration.getRootFolder(), dto);

            repository.save(new DocumentEntity(documentId,
                    dto.getFile().getOriginalFilename(),
                    dto.getFile().getSize()
                    , dto.getFile().getContentType()));

            responseEntity = new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK);
        } else if (byFolderId.isPresent()) {

            FolderEntity folderEntity = byFolderId.get();
            Folder folderById = alfrescoFolderService.getFolderById(dto.getFolderId());

            String documentId = alfrescoDocumentService.create(folderById, dto);

            repository.save(new DocumentEntity(documentId,
                    dto.getFile().getOriginalFilename(),
                    dto.getFile().getSize()
                    , dto.getFile().getContentType(),
                    folderEntity));

            responseEntity = new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK);
        }

        return responseEntity;

    }

    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> update(DocumentUpdateDTO dto) {


        Optional<DocumentEntity> optionalDocument = repository.findByDocumentId(dto.getDocumentId());

        if (optionalDocument.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        alfrescoDocumentService.update(dto);

        DocumentEntity documentEntity = optionalDocument.get();
        documentEntity.setContentType(dto.getFile().getContentType());
        documentEntity.setName(dto.getFile().getOriginalFilename());
        documentEntity.setSize(dto.getFile().getSize());
        repository.save(documentEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> delete(String documentId) {

        Optional<DocumentEntity> byDocumentId = repository.findByDocumentId(documentId);

        if (byDocumentId.isPresent()) {


            alfrescoDocumentService.delete(documentId);
            DocumentEntity documentEntity = byDocumentId.get();
            repository.delete(documentEntity);
        }

        return null;
    }

    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> getDocumentsByFolderId(String folderId) {

        List<DocumentDTO> documents = repository.getDocumentsByFolderId(folderId);

        for (DocumentDTO document : documents) {
            List<String> versions = new ArrayList<>();

            Document documentById = alfrescoDocumentService.getDocumentById(document.getDocumentId(), null);

            List<Document> allVersions = documentById.getAllVersions();
            for (Document allVersion : allVersions) {
                versions.add(allVersion.getVersionLabel());
            }
            document.setVersions(versions);
        }

        return documents.isEmpty() ? new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value()), HttpStatus.OK) :
                new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), documents), HttpStatus.OK);
    }
}
