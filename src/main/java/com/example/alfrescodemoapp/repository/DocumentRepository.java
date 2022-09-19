package com.example.alfrescodemoapp.repository;

import com.example.alfrescodemoapp.dto.DocumentDTO;
import com.example.alfrescodemoapp.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {


    @Query("select new com.example.alfrescodemoapp.dto.DocumentDTO(d.id,d.documentId,d.name,d.size,d.contentType) from DocumentEntity d ")
    List<DocumentDTO> getAll();


    Optional<DocumentEntity> findByDocumentId(String documentId);

    @Query("select new com.example.alfrescodemoapp.dto.DocumentDTO(d.id,d.documentId,d.name,d.size,d.contentType) from DocumentEntity d where d.folder.id=:folderId")
    List<DocumentDTO> getDocumentsByFolderId(Long folderId);

    @Query("select d.folder.folderId from DocumentEntity d where d.id=:id")
    Optional<String> getId(String id);



    @Modifying
    @Transactional
    void deleteAllByDocumentIdIn(List<String> documentIds);


}
