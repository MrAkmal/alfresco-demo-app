package com.example.alfrescodemoapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alfresco_document")
public class DocumentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String documentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String contentType;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH,CascadeType.REMOVE},
            fetch = FetchType.LAZY)

    private FolderEntity folder;


    public DocumentEntity(String documentId, String name, long size, String contentType, FolderEntity folder) {
        this.documentId = documentId;
        this.name = name;
        this.size = size;
        this.contentType = contentType;
        this.folder = folder;
    }

    public DocumentEntity(String documentId, String name, long size, String contentType) {
        this.documentId = documentId;
        this.name = name;
        this.size = size;
        this.contentType = contentType;
    }
}
