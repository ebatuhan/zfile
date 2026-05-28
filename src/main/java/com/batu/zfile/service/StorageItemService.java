package com.batu.zfile.service;

import java.util.UUID;

import com.batu.zfile.dto.FolderResponseDTO;
import com.batu.zfile.dto.StorageItemRequestDTO;
import com.batu.zfile.dto.StorageItemResponseDTO;

public interface StorageItemService {
    FolderResponseDTO getStorageItems();
    
    FolderResponseDTO getStorageItems(UUID parentId);

    StorageItemResponseDTO updateStorageItem(UUID storageItemId, StorageItemRequestDTO request);

    void deleteStorageItem(UUID storageItemId);
}
