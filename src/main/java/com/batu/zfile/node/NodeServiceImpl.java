package com.batu.zfile.node;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.common.CursorPageResponse;
import com.batu.zfile.common.CursorUtils;
import com.batu.zfile.common.ResourceNotFoundException;
import com.batu.zfile.metadata.FileMetadata;
import com.batu.zfile.node.dto.NodeDTO;
import com.batu.zfile.node.dto.NodeDetailsDTO;
import com.batu.zfile.node.dto.NodeDownloadDTO;
import com.batu.zfile.node.dto.NodePathDTO;
import com.batu.zfile.node.dto.NodeRequestDTO;
import com.batu.zfile.node.event.NodeCreatedEvent;
import com.batu.zfile.storage.ObjectStorageService;
import com.batu.zfile.storage.PresignedUrl;
import com.batu.zfile.storage.StoredObject;

public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final ObjectStorageService objectStorageService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CursorUtils cursorUtils;
    private final NodeMapper nodeMapper;
    private final int PATH_MAX_DEPTH = 5;

    public NodeServiceImpl(NodeRepository nodeRepository, CursorUtils cursorUtils, NodeMapper nodeMapper,
            ObjectStorageService objectStorageService, ApplicationEventPublisher applicationEventPublisher) {
        this.nodeRepository = nodeRepository;
        this.objectStorageService = objectStorageService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cursorUtils = cursorUtils;
        this.nodeMapper = nodeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<NodeDTO> getNodes(UUID parentId, String cursorPosition) {

        Node parentNode = nodeRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent id is not found."));

        if (parentNode.getType() == NodeType.FILE) {
            throw new IllegalStateException("Requested resource is not a folder.");
        }

        KeysetScrollPosition decodedPosition = cursorUtils.getPosition(cursorPosition);

        Window<Node> nodes = nodeRepository.findFirst20ByParentIdOrderByCreatedAtDescNodeIdDesc(
                parentId,
                decodedPosition);

        final String nextCursor = cursorUtils.getNextCursor(nodes);

        List<NodeDTO> nodeResponse = nodes.stream()
                .map(nodeMapper::toNodeItemDto)
                .toList();

        return new CursorPageResponse<NodeDTO>(nodeResponse, nextCursor, nodes.hasNext());
    }

    @Override
    @Transactional(readOnly = true)
    public NodeDTO getNodeById(UUID nodeId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node not found."));
        return nodeMapper.toNodeItemDto(node);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NodePathDTO> getPathByNodeId(UUID nodeId) {
        return nodeRepository.findPathByNodeId(nodeId, PATH_MAX_DEPTH)
                .stream()
                .map(nodeMapper::toNodePathDTO)
                .toList();
    }

    @Override
    @Transactional
    public NodeDTO updateNodeById(UUID nodeId, NodeRequestDTO request) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found."));

        Node parentNode = null;

        if (request.parentId() != null) {
            parentNode = nodeRepository.findById(nodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent node not found."));
        }

        node.setParent(parentNode);
        node.setName(request.name());

        Node updatedNode = nodeRepository.save(node);

        return nodeMapper.toNodeItemDto(updatedNode);
    }

    @Override
    @Transactional(readOnly = true)
    public NodeDetailsDTO getNodeDetails(UUID nodeId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node not found."));

        return nodeMapper.toNodeDetailsDTO(node);
    }

    @Override
    @Transactional(readOnly = true)
    public NodeDownloadDTO downloadNodeFile(UUID nodeId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node not found."));

        if (node.getType() == NodeType.FOLDER) {
            throw new IllegalStateException("Requested resource is not a folder.");
        }

        final String objectKey = node.getMetadata().getObjectKey();

        final PresignedUrl presignedUrl = objectStorageService.getFileLink(objectKey);

        return new NodeDownloadDTO(
                presignedUrl.url(),
                presignedUrl.expiresAt());
    }

    @Override
    @Transactional
    public NodeDTO createFileNode(UUID parentId, MultipartFile file) {

        Node parentNode = null;

        if (parentId != null) {
            parentNode = nodeRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent node not found."));
        }

        if (parentNode.getType() == NodeType.FOLDER) {
            throw new IllegalStateException("Requested resource is not a folder.");
        }

        StoredObject storedFile = objectStorageService.storeFile(file);

        FileMetadata fileMetadata = FileMetadata.builder()
                .contentType(storedFile.mimeType())
                .size(storedFile.sizeInBytes())
                .objectKey(storedFile.objectKey())
                .build();

        Node fileNode = Node.builder()
                .name(file.getName())
                .parent(parentNode)
                .metadata(fileMetadata)
                .build();

        Node savedNode = nodeRepository.save(fileNode);

        applicationEventPublisher.publishEvent(new NodeCreatedEvent(savedNode.getNodeId()));

        return nodeMapper.toNodeItemDto(savedNode);

    }

    @Override
    @Transactional
    public NodeDTO createFolderNode(NodeRequestDTO request) {

        Node parentNode = null;

        if (request.parentId() != null) {
            parentNode = nodeRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found."));
        }

        if (parentNode.getType() == NodeType.FOLDER) {
            throw new IllegalStateException("Requested resource is not a folder.");
        }

        Node folderNode = Node.builder()
                .name(request.name())
                .parent(parentNode)
                .type(NodeType.FOLDER)
                .build();

        return nodeMapper.toNodeItemDto(folderNode);
    }

    @Override
    @Transactional
    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    @Transactional
    public Node read(UUID nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node not found."));
    }
}
