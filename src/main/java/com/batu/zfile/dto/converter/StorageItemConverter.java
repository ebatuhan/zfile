package com.batu.zfile.dto.converter;

import org.springframework.stereotype.Component;

import com.batu.zfile.dto.StorageItemResponseDTO;
import com.batu.zfile.entity.StorageItem;

@Component
public class StorageItemConverter {
    public StorageItemResponseDTO toResponseDTO(StorageItem from) {
        return StorageItemResponseDTO.builder()
                .name(from.getName())
                .storageItemId(from.getStorageItemId())
                .itemType(from.getItemType())
                .parentId(from.getParentItem() != null
                        ? from.getParentItem().getStorageItemId()
                        : null)
                .build();
    }
}
