package com.batu.zfile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FolderResponseDTO {
    private StorageItemResponseDTO parent;
    private List<StorageItemResponseDTO> content;
}
