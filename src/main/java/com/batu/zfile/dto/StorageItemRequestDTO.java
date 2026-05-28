package com.batu.zfile.dto;

import java.util.UUID;

import com.batu.zfile.entity.ItemType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StorageItemRequestDTO {

    private UUID storageItemId;

    private String name;
    
    private UUID parentId;

    private ItemType itemType;

    
}
