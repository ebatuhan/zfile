package com.batu.zfile.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batu.zfile.dto.CreateFolderRequest;
import com.batu.zfile.dto.FileNodeChildrenResponse;
import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.UpdateNodeRequest;
import com.batu.zfile.service.FileNodeService;

@RestController
@RequestMapping("/api/nodes")
public class FileNodeController {

    private final FileNodeService fileNodeService;

    public FileNodeController(FileNodeService fileNodeService) {
        this.fileNodeService = fileNodeService;
    }

    @GetMapping("/children")
    public FileNodeChildrenResponse getRootChildren() {
        return fileNodeService.getRootChildren();
    }

    @GetMapping("/{parentId}/children")
    public FileNodeChildrenResponse getChildren(@PathVariable UUID parentId) {
        return fileNodeService.getChildren(parentId);
    }

    @GetMapping("/{nodeId}")
    public FileNodeResponse getNode(@PathVariable UUID nodeId) {
        return fileNodeService.getNode(nodeId);
    }

    @PostMapping("/{parentId}/folders")
    public FileNodeResponse createFolder(@PathVariable UUID parentId, @RequestBody CreateFolderRequest request) {
        return fileNodeService.createFolder(parentId, request);
    }

    @PatchMapping("/{nodeId}/name")
    public FileNodeResponse renameNode(@PathVariable UUID nodeId, @RequestBody UpdateNodeRequest request) {
        return fileNodeService.renameNode(nodeId, request);
    }

    @PatchMapping("/{nodeId}/parent")
    public FileNodeResponse moveNode(@PathVariable UUID nodeId, @RequestBody UpdateNodeRequest request) {
        return fileNodeService.moveNode(nodeId, request);
    }

    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable UUID nodeId) {
        fileNodeService.deleteNode(nodeId);
        return ResponseEntity.noContent().build();
    }
}
