package com.batu.zfile.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batu.zfile.dto.FolderResponseDTO;
import com.batu.zfile.dto.StorageItemRequestDTO;
import com.batu.zfile.dto.StorageItemResponseDTO;
import com.batu.zfile.service.StorageItemService;

@RestController
@RequestMapping("/api/items")
public class StorageItemController {

    private final StorageItemService storageItemService;

    public StorageItemController(StorageItemService storageItemService) {
        this.storageItemService = storageItemService;
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<FolderResponseDTO> getStorageItems(@PathVariable UUID parentId){
        return ResponseEntity.ok(storageItemService.getStorageItems(parentId));
    }

    @GetMapping("/children")
    public ResponseEntity<FolderResponseDTO> getStorageItems(){
        return ResponseEntity.ok(storageItemService.getStorageItems());
    }

    @PatchMapping("/{storageItemId}")
    public ResponseEntity<StorageItemResponseDTO> updateStorageItem(@PathVariable UUID storageItemId, @RequestBody StorageItemRequestDTO request){
        return ResponseEntity.ok(storageItemService.updateStorageItem(storageItemId, request));
    }

    @DeleteMapping("/{storageItemId}")
    public ResponseEntity<Void> deleteStorageItem(@PathVariable UUID storageItemId){
        storageItemService.deleteStorageItem(storageItemId);
        return ResponseEntity.noContent().build();
    }
}
