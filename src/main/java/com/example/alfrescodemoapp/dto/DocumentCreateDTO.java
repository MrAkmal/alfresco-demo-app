package com.example.alfrescodemoapp.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DocumentCreateDTO {
    private String folderId;
    private MultipartFile file;
}
