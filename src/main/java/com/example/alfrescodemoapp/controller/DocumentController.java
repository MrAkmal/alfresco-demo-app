package com.example.alfrescodemoapp.controller;

import com.example.alfrescodemoapp.dto.*;
import com.example.alfrescodemoapp.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/alfresco/document")
@CrossOrigin("*")
public class DocumentController {


    private final DocumentService service;


    @Autowired
    public DocumentController(DocumentService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> getAll() {
        return service.getAll();
    }

    @GetMapping("/{documentId}/{version}")
    public @ResponseBody ResponseEntity download(@PathVariable String documentId,@PathVariable String version) {
        System.out.println("version = " + version);
        return service.download(documentId,version);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> create(@RequestBody MultipartFile multipartFile, @RequestParam String folderId) {

        return service.create(new DocumentCreateDTO(folderId,multipartFile));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<DocumentDTO>, ErrorDTO>> update(@RequestBody MultipartFile multipartFile, @RequestParam String documentId) {
        return service.update(new DocumentUpdateDTO(documentId,multipartFile));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String documentId) {
        return service.delete(documentId);
    }

    @GetMapping("/documents/{folderId}")
    public ResponseEntity<ApiResponse<List<DocumentDTO>,ErrorDTO>> getDocumentsByFolderId(@PathVariable String folderId) {
        return service.getDocumentsByFolderId(folderId);
    }
}
