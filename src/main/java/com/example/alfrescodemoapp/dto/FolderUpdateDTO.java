package com.example.alfrescodemoapp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FolderUpdateDTO {

    private String folderId;

    private String name;


}
