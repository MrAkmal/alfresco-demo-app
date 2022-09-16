package com.example.alfrescodemoapp.service;


import com.example.alfrescodemoapp.config.AlfrescoConfiguration;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlfrescoFolderService {


    public static List<String> foldersId = new ArrayList<>();
    public static List<String> documentsId = new ArrayList<>();


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


    public void getAllChildIds(Folder folder) {

        List<Tree<FileableCmisObject>> descendants = folder.getDescendants(1000);
        if (!descendants.isEmpty()) {

            for (Tree<FileableCmisObject> descendant : descendants) {
                if (descendant.getItem() instanceof Folder) {
                    foldersId.add(descendant.getItem().getId());
                    if (!descendant.getChildren().isEmpty())
                        getAllChildIds((Folder) descendant.getItem());
                } else documentsId.add(descendant.getItem().getId().substring(0,descendant.getItem().getId().indexOf(";")));
            }
        } else {
            foldersId.add(folder.getId());
        }


    }


}
