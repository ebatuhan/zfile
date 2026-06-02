package com.batu.zfile.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import com.batu.zfile.dto.CreateFolderRequest;
import com.batu.zfile.dto.FileDownloadResponse;
import com.batu.zfile.dto.FileNodeChildrenResponse;
import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.UpdateNodeRequest;
import com.batu.zfile.dto.converter.FileNodeConverter;
import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.entity.FileNode;
import com.batu.zfile.entity.NodeType;
import com.batu.zfile.entity.ThumbnailStatus;
import com.batu.zfile.event.FileUploaded;
import com.batu.zfile.repository.FileNodeRepository;
import com.batu.zfile.repository.PendingObjectDeletionRepository;
import com.batu.zfile.service.FileNodeService;
import com.batu.zfile.service.ObjectBucket;
import com.batu.zfile.service.ObjectStorageService;
import com.batu.zfile.service.ThumbnailService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FileNodeServiceImpl implements FileNodeService {

    private final FileNodeRepository fileNodeRepository;
    private final PendingObjectDeletionRepository pendingObjectDeletionRepository;
    private final ObjectStorageService objectStorageService;
    private final ThumbnailService thumbnailService;
    private final FileNodeConverter fileNodeConverter;
    private final ApplicationEventPublisher applicationEventPublisher;

    public FileNodeServiceImpl(
            FileNodeRepository fileNodeRepository,
            PendingObjectDeletionRepository pendingObjectDeletionRepository,
            ObjectStorageService objectStorageService,
            ThumbnailService thumbnailService,
            FileNodeConverter fileNodeConverter,
            ApplicationEventPublisher applicationEventPublisher) {
        this.fileNodeRepository = fileNodeRepository;
        this.pendingObjectDeletionRepository = pendingObjectDeletionRepository;
        this.objectStorageService = objectStorageService;
        this.thumbnailService = thumbnailService;
        this.fileNodeConverter = fileNodeConverter;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public FileNodeChildrenResponse getRootChildren() {
        var childNodes = fileNodeRepository.findByParentIsNullOrderByTypeAscNameAsc();
        var thumbnailUrls = thumbnailService.getThumbnailUrls(childNodes.stream()
                .map(FileNode::getMetadata)
                .filter(metadata -> metadata != null)
                .toList());
        var children = childNodes
                .stream()
                .map(node -> fileNodeConverter.toResponse(
                        node,
                        node.getMetadata() == null ? null : thumbnailUrls.get(node.getMetadata().getFileMetadataId())))
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
        var childNodes = fileNodeRepository.findByParent_FileNodeIdOrderByTypeAscNameAsc(parentId);
        var childThumbnailUrls = thumbnailService.getThumbnailUrls(childNodes.stream()
                .map(FileNode::getMetadata)
                .filter(metadata -> metadata != null)
                .toList());
        var children = childNodes
                .stream()
                .map(node -> fileNodeConverter.toResponse(
                        node,
                        node.getMetadata() == null ? null : childThumbnailUrls.get(node.getMetadata().getFileMetadataId())))
                .toList();

        return new FileNodeChildrenResponse(fileNodeConverter.toResponse(current), path, children);
    }

    @Override
    @Transactional(readOnly = true)
    public FileNodeResponse getNode(UUID nodeId) {
        var node = fileNodeRepository.findWithMetadataById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + nodeId));
        var thumbnailUrls = thumbnailService.getThumbnailUrls(node.getMetadata() == null ? List.of() : List.of(node.getMetadata()));

        return fileNodeConverter.toResponse(
                node,
                node.getMetadata() == null ? null : thumbnailUrls.get(node.getMetadata().getFileMetadataId()));
    }

    @Override
    @Transactional(readOnly = true)
    public FileDownloadResponse downloadFile(UUID nodeId) {
        var node = fileNodeRepository.findWithMetadataById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("File node not found: " + nodeId));

        if (node.getType() != NodeType.FILE) {
            throw new IllegalArgumentException("File node is not a file: " + nodeId);
        }

        var metadata = node.getMetadata();
        if (metadata == null) {
            throw new EntityNotFoundException("File metadata not found for node: " + nodeId);
        }

        return new FileDownloadResponse(
                node.getName(),
                metadata.getSize(),
                metadata.getContentType(),
                objectStorageService.createPresignedUrl(ObjectBucket.FILE, metadata.getObjectKey()));
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
    public FileNodeResponse uploadFile(UUID parentId, MultipartFile file) {
        FileNode parent = null;

        if (parentId != null) {
            parent = fileNodeRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent file node not found: " + parentId));

            if (parent.getType() != NodeType.FOLDER) {
                throw new IllegalArgumentException("Parent file node is not a folder: " + parentId);
            }
        }

        var fileNode = FileNode.builder()
                .parent(parent)
                .type(NodeType.FILE)
                .name(file.getOriginalFilename())
                .build();

        var metadata = objectStorageService.upload(file);
        metadata.setNode(fileNode);
        metadata.setThumbnail(thumbnailService.createFor(metadata));
        fileNode.setMetadata(metadata);

        var savedNode = fileNodeRepository.save(fileNode);
        if (savedNode.getMetadata().getThumbnail().getStatus() == ThumbnailStatus.PENDING) {
            applicationEventPublisher.publishEvent(new FileUploaded(savedNode.getMetadata().getFileMetadataId()));
        }

        return fileNodeConverter.toResponse(savedNode);
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

        var thumbnailUrls = thumbnailService.getThumbnailUrls(node.getMetadata() == null ? List.of() : List.of(node.getMetadata()));
        return fileNodeConverter.toResponse(
                node,
                node.getMetadata() == null ? null : thumbnailUrls.get(node.getMetadata().getFileMetadataId()));
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
        var thumbnailUrls = thumbnailService.getThumbnailUrls(node.getMetadata() == null ? List.of() : List.of(node.getMetadata()));
        return fileNodeConverter.toResponse(
                node,
                node.getMetadata() == null ? null : thumbnailUrls.get(node.getMetadata().getFileMetadataId()));
    }

    @Override
    @Transactional
    public void deleteNode(UUID nodeId) {
        pendingObjectDeletionRepository.enqueueObjectsForDeletedNode(nodeId);
        fileNodeRepository.deleteById(nodeId);
    }
}
