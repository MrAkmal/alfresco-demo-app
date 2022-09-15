package com.example.alfrescodemoapp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FolderCreateDTO {


    private String name;

    private String parentFolderId;


}
