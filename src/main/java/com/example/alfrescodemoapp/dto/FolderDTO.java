package com.example.alfrescodemoapp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderDTO {

    private Long id;

    private String name;

    private String parentId;

    private String folderId;


}
