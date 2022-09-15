package com.example.alfrescodemoapp.dto;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DocumentDTO {

    private Long id;

    private String documentId;

    private String name;

    private long size;

    private String contentType;

    private List<String> versions;

    public DocumentDTO(Long id, String documentId, String name, long size, String contentType) {
        this.id = id;
        this.documentId = documentId;
        this.name = name;
        this.size = size;
        this.contentType = contentType;
    }
}
