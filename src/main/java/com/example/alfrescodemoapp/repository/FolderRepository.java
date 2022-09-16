package com.example.alfrescodemoapp.repository;

import com.example.alfrescodemoapp.dto.FolderDTO;
import com.example.alfrescodemoapp.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {


    @Query("select new com.example.alfrescodemoapp.dto.FolderDTO(f.id,f.name,f.parentId,f.folderId) from FolderEntity f")
    List<FolderDTO> getAll();


    @Query("select new com.example.alfrescodemoapp.dto.FolderDTO(f.id,f.name,f.parentId,f.folderId) from FolderEntity f where f.folderId = :folderId")
    FolderDTO getByFolderId(String folderId);


    Optional<FolderEntity> findByFolderId(String folderId);

    Optional<FolderEntity> findByParentIdAndAndName(String parentId, String name);

    @Query("select new com.example.alfrescodemoapp.dto.FolderDTO(f.id,f.name,f.parentId,f.folderId) from FolderEntity f where f.parentId = :parentId")
    List<FolderDTO> findAllByParentId(String parentId);

    @Query("select new com.example.alfrescodemoapp.dto.FolderDTO(f.id,f.name,f.parentId,f.folderId) from FolderEntity f where f.parentId = '0'")
    List<FolderDTO> getMainFolders();
}
