package com.example.alfrescodemoapp.service;


import com.example.alfrescodemoapp.config.AlfrescoConfiguration;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlfrescoFolderService {

    private final AlfrescoConfiguration alfrescoConfiguration;


    @Autowired
    public AlfrescoFolderService(AlfrescoConfiguration alfrescoConfiguration) {
        this.alfrescoConfiguration = alfrescoConfiguration;
    }

    public String createFolder(Folder parentFolder, String folderName) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        Folder folder = parentFolder.createFolder(properties);
        return folder.getId();
    }

    public String createFolder(String folderName) {
        return createFolder(alfrescoConfiguration.getRootFolder(), folderName);
    }

    public Folder getFolderById(String folderId) {
        return (Folder) alfrescoConfiguration.session.getObject(alfrescoConfiguration.session.createObjectId(folderId));
    }

    public void delete(CmisObject cmisObject) {

        if (BaseTypeId.CMIS_FOLDER.equals(cmisObject.getBaseTypeId())) {

            Folder folder = (Folder) cmisObject;

            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject child : children) {
                delete(child);
            }

        }
        alfrescoConfiguration.session.delete(cmisObject);
    }

    public void update(String folderId, String name) {

        Folder folder = getFolderById(folderId);

        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.NAME, name);
        folder.updateProperties(properties);

    }
}
