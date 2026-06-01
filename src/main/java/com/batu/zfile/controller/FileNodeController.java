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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.dto.CreateFolderRequest;
import com.batu.zfile.dto.FileDownloadResponse;
import com.batu.zfile.dto.FileNodeChildrenResponse;
import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.UpdateNodeRequest;
import com.batu.zfile.service.FileNodeService;

//TODO handle ROOT folder case by NULLS

@RestController
@RequestMapping("/api/nodes")
public class FileNodeController {

    private final FileNodeService fileNodeService;

    public FileNodeController(FileNodeService fileNodeService) {
        this.fileNodeService = fileNodeService;
    }

    @GetMapping("/children")
    public ResponseEntity<FileNodeChildrenResponse> getRootChildren() {
        return ResponseEntity.ok(fileNodeService.getRootChildren());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<FileNodeChildrenResponse> getChildren(@PathVariable UUID parentId) {
        return ResponseEntity.ok(fileNodeService.getChildren(parentId));
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<FileNodeResponse> getNode(@PathVariable UUID nodeId) {
        return ResponseEntity.ok(fileNodeService.getNode(nodeId));
    }

    @GetMapping("/{nodeId}/content")
    public ResponseEntity<FileDownloadResponse> downloadFile(@PathVariable UUID nodeId) {
        return ResponseEntity.ok(fileNodeService.downloadFile(nodeId));
    }

    @PostMapping("/{parentId}/folders")
    public ResponseEntity<FileNodeResponse> createFolder(@PathVariable UUID parentId, @RequestBody CreateFolderRequest request) {
        return ResponseEntity.ok(fileNodeService.createFolder(parentId, request));
    }

    @PostMapping("/folders")
    public ResponseEntity<FileNodeResponse> createRootFolder(@RequestBody CreateFolderRequest request) {
        return ResponseEntity.ok(fileNodeService.createFolder(null, request));
    }

    @PostMapping("/files")
    public ResponseEntity<FileNodeResponse> uploadRootFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileNodeService.uploadFile(null, file));
    }

    @PostMapping("/{parentId}/files")
    public ResponseEntity<FileNodeResponse> uploadFile(@PathVariable UUID parentId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileNodeService.uploadFile(parentId, file));
    }

    @PatchMapping("/{nodeId}/name")
    public ResponseEntity<FileNodeResponse> renameNode(@PathVariable UUID nodeId, @RequestBody UpdateNodeRequest request) {
        return ResponseEntity.ok(fileNodeService.renameNode(nodeId, request));
    }

    @PatchMapping("/{nodeId}/parent")
    public ResponseEntity<FileNodeResponse> moveNode(@PathVariable UUID nodeId, @RequestBody UpdateNodeRequest request) {
        return ResponseEntity.ok(fileNodeService.moveNode(nodeId, request));
    }

    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable UUID nodeId) {
        fileNodeService.deleteNode(nodeId);
        return ResponseEntity.noContent().build();
    }
}
