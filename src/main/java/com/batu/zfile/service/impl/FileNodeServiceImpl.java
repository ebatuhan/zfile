package com.batu.zfile.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batu.zfile.dto.CreateFolderRequest;
import com.batu.zfile.dto.FileNodeChildrenResponse;
import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.UpdateNodeRequest;
import com.batu.zfile.dto.converter.FileNodeConverter;
import com.batu.zfile.entity.FileNode;
import com.batu.zfile.entity.NodeType;
import com.batu.zfile.repository.FileNodeRepository;
import com.batu.zfile.repository.PendingObjectDeletionRepository;
import com.batu.zfile.service.FileNodeService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FileNodeServiceImpl implements FileNodeService {

    private final FileNodeRepository fileNodeRepository;
    private final PendingObjectDeletionRepository pendingObjectDeletionRepository;
    private final FileNodeConverter fileNodeConverter;

    public FileNodeServiceImpl(
            FileNodeRepository fileNodeRepository,
            PendingObjectDeletionRepository pendingObjectDeletionRepository,
            FileNodeConverter fileNodeConverter) {
        this.fileNodeRepository = fileNodeRepository;
        this.pendingObjectDeletionRepository = pendingObjectDeletionRepository;
        this.fileNodeConverter = fileNodeConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public FileNodeChildrenResponse getRootChildren() {
        var children = fileNodeRepository.findByParentIsNullOrderByTypeAscNameAsc()
                .stream()
                .map(fileNodeConverter::toResponse)
                .toList();

        return new FileNodeChildrenResponse(null, List.of(), children);
    }

    @Override
    @Transactional(readOnly = true)
    public FileNodeChildrenResponse getChildren(UUID parentId) {
        var current = fileNodeRepository.findWithMetadataById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + parentId));

        if (current.getType() != NodeType.FOLDER) {
            throw new IllegalArgumentException("File node is not a folder: " + parentId);
        }

        var path = fileNodeRepository.findPathById(parentId)
                .stream()
                .map(fileNodeConverter::toPathSegmentResponse)
                .toList();
        var children = fileNodeRepository.findByParent_FileNodeIdOrderByTypeAscNameAsc(parentId)
                .stream()
                .map(fileNodeConverter::toResponse)
                .toList();

        return new FileNodeChildrenResponse(fileNodeConverter.toResponse(current), path, children);
    }

    @Override
    @Transactional(readOnly = true)
    public FileNodeResponse getNode(UUID nodeId) {
        return fileNodeRepository.findWithMetadataById(nodeId)
                .map(fileNodeConverter::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + nodeId));
    }

    @Override
    @Transactional
    public FileNodeResponse createFolder(UUID parentId, CreateFolderRequest request) {
        FileNode parent = null;

        if (parentId != null) {
            parent = fileNodeRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent file node not found: " + parentId));

            if (parent.getType() != NodeType.FOLDER) {
                throw new IllegalArgumentException("Parent file node is not a folder: " + parentId);
            }
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        var folder = FileNode.builder()
                .parent(parent)
                .type(NodeType.FOLDER)
                .name(request.name().trim())
                .build();

        return fileNodeConverter.toResponse(fileNodeRepository.save(folder));
    }

    @Override
    @Transactional
    public FileNodeResponse renameNode(UUID nodeId, UpdateNodeRequest request) {
        var node = fileNodeRepository.findWithMetadataById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + nodeId));

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        node.setName(request.name().trim());

        return fileNodeConverter.toResponse(node);
    }

    @Override
    @Transactional
    public FileNodeResponse moveNode(UUID nodeId, UpdateNodeRequest request) {
        var node = fileNodeRepository.findWithMetadataById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + nodeId));
        FileNode parent = null;

        if (request.parentId() != null) {
            parent = fileNodeRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent file node not found: " + request.parentId()));

            if (parent.getType() != NodeType.FOLDER) {
                throw new IllegalArgumentException("Parent file node is not a folder: " + request.parentId());
            }

            if (node.getFileNodeId().equals(request.parentId())) {
                throw new IllegalArgumentException("A node cannot be moved into itself");
            }

            if (node.getType() == NodeType.FOLDER
                    && fileNodeRepository.existsByFileNodeIdInAncestorChain(request.parentId(), nodeId)) {
                throw new IllegalArgumentException("A folder cannot be moved into its descendant");
            }
        }

        node.setParent(parent);
        return fileNodeConverter.toResponse(node);
    }

    @Override
    @Transactional
    public void deleteNode(UUID nodeId) {
        pendingObjectDeletionRepository.enqueueObjectsForDeletedNode(nodeId);
        fileNodeRepository.deleteById(nodeId);
    }
}
