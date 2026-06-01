package com.batu.zfile.service;

import java.util.UUID;

import com.batu.zfile.dto.CreateFolderRequest;
import com.batu.zfile.dto.FileNodeChildrenResponse;
import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.UpdateNodeRequest;

public interface FileNodeService {

    FileNodeChildrenResponse getRootChildren();

    FileNodeChildrenResponse getChildren(UUID parentId);

    FileNodeResponse getNode(UUID nodeId);

    FileNodeResponse createFolder(UUID parentId, CreateFolderRequest request);

    FileNodeResponse renameNode(UUID nodeId, UpdateNodeRequest request);

    FileNodeResponse moveNode(UUID nodeId, UpdateNodeRequest request);

    void deleteNode(UUID nodeId);
}
