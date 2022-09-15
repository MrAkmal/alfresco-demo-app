package com.example.alfrescodemoapp.service;


import com.example.alfrescodemoapp.config.AlfrescoConfiguration;
import com.example.alfrescodemoapp.dto.DocumentCreateDTO;
import com.example.alfrescodemoapp.dto.DocumentUpdateDTO;
import lombok.SneakyThrows;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AlfrescoDocumentService {

    private final AlfrescoConfiguration alfrescoConfiguration;


    @Autowired
    public AlfrescoDocumentService(AlfrescoConfiguration alfrescoConfiguration) {
        this.alfrescoConfiguration = alfrescoConfiguration;
    }


    public Document getDocumentById(String documentId, String version) {
        if (version != null)
            return (Document) alfrescoConfiguration.session.getObject(alfrescoConfiguration.session.createObjectId(documentId + ";" + version));
        return (Document) alfrescoConfiguration.session.getObject(alfrescoConfiguration.session.createObjectId(documentId));
    }


    @SneakyThrows
    public String create(Folder folder, DocumentCreateDTO dto) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, dto.getFile().getOriginalFilename());

        MultipartFile file = dto.getFile();

        InputStream inputStream = file.getInputStream();


        byte[] content = dto.getFile().getBytes();

        ContentStream contentStream =
                new ContentStreamImpl(dto.getFile().getOriginalFilename(), BigInteger.valueOf(content.length),
                        dto.getFile().getContentType(),
                        inputStream);


        Document document = folder.createDocument(properties, contentStream, VersioningState.MAJOR);


        System.out.println("document.getVersionLabel() = " + document.getVersionLabel());
        System.out.println("document.getVersionSeriesId() = " + document.getVersionSeriesId());

        String id = document.getId();
        System.out.println("id = " + id);
        return id.substring(0, id.indexOf(";"));
    }


    @SneakyThrows
    public void update(DocumentUpdateDTO dto) {

        Document document = getDocumentById(dto.getDocumentId(), null);

        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.NAME, dto.getFile().getOriginalFilename());

        MultipartFile file = dto.getFile();

        InputStream inputStream = file.getInputStream();


        byte[] content = dto.getFile().getBytes();

        ContentStream contentStream =
                new ContentStreamImpl(dto.getFile().getOriginalFilename(), BigInteger.valueOf(content.length),
                        dto.getFile().getContentType(),
                        inputStream);

        document.setContentStream(contentStream, true);

        Document documentById = getDocumentById(dto.getDocumentId(), null);

        documentById.updateProperties(properties);

    }

    public void delete(String documentId) {

        Document documentById = getDocumentById(documentId, null);
        alfrescoConfiguration.session.delete(documentById);

    }
}
