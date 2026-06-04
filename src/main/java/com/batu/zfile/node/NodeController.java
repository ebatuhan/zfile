package com.batu.zfile.node;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batu.zfile.common.CursorPageResponse;
import com.batu.zfile.node.dto.NodeDTO;
import com.batu.zfile.node.dto.NodeDetailsDTO;
import com.batu.zfile.node.dto.NodeDownloadDTO;
import com.batu.zfile.node.dto.NodePathDTO;
import com.batu.zfile.node.dto.NodeRequestDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<NodeDetailsDTO> getNodeDetails(@PathVariable UUID nodeId) {
        return ResponseEntity.ok(nodeService.getNodeDetails(nodeId));
    }

    @GetMapping("/{nodeId}/download")
    public ResponseEntity<NodeDownloadDTO> downloadNode(@PathVariable UUID nodeId) {
        return ResponseEntity.ok(nodeService.downloadNodeFile(nodeId));
    }

    @GetMapping("/{nodeId}/children")
    public ResponseEntity<CursorPageResponse<NodeDTO>> getNodes(@PathVariable UUID nodeId,
            @RequestParam String cursor) {
        return ResponseEntity.ok(nodeService.getNodes(nodeId, cursor));
    }

    @GetMapping("/path/{nodeId}")
    public ResponseEntity<List<NodePathDTO>> getNodePath(@PathVariable UUID nodeId) {
        return ResponseEntity.ok(nodeService.getPathByNodeId(nodeId));
    }

    @PatchMapping("/{nodeId}")
    public ResponseEntity<NodeDTO> updateNode(@PathVariable UUID nodeId, @RequestBody NodeRequestDTO request) {
        return ResponseEntity.ok(nodeService.updateNodeById(nodeId, request));
    }

}
