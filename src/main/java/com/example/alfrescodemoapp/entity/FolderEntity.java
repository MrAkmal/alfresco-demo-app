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
@Table(name = "alfresco_folder")
public class FolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String parentId;

    @Column(nullable = false)
    private String folderId;

    public FolderEntity(String name, String parentId, String folderId) {
        this.name = name;
        this.parentId = parentId;
        this.folderId = folderId;
    }
}
