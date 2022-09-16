package com.example.alfrescodemoapp.controller;

import com.example.alfrescodemoapp.dto.*;
import com.example.alfrescodemoapp.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1/alfresco/folder")
public class FolderController {

    private final FolderService service;

    @Autowired
    public FolderController(FolderService service) {
        this.service = service;
    }


    @GetMapping("/{folderId}")
    public ResponseEntity<ApiResponse<FolderDTO,ErrorDTO>> get(@PathVariable String folderId) {
        return service.get(folderId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> create(@RequestBody FolderCreateDTO dto) {
        return service.create(dto);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> update(@RequestBody FolderUpdateDTO dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> delete(@PathVariable String folderId) {
        return service.delete(folderId);
    }

    @GetMapping("/sub_folders/{folderId}")
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> getSubFolders(@PathVariable String folderId) {
        return service.getSubFolders(folderId);
    }

    @GetMapping("/main_folders")
    public ResponseEntity<ApiResponse<List<FolderDTO>,ErrorDTO>> getMainFolders() {
        return service.getMainFolders();
    }


}
