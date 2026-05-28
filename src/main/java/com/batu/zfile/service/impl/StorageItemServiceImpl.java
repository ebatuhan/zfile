package com.batu.zfile.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batu.zfile.dto.FolderResponseDTO;
import com.batu.zfile.dto.StorageItemRequestDTO;
import com.batu.zfile.dto.StorageItemResponseDTO;
import com.batu.zfile.dto.converter.StorageItemConverter;
import com.batu.zfile.entity.ItemType;
import com.batu.zfile.entity.StorageItem;
import com.batu.zfile.repository.StorageItemRepository;
import com.batu.zfile.service.StorageItemService;


@Service
public class StorageItemServiceImpl implements StorageItemService {

    private final String ROOT_FOLDER_NAME = "Home";

    private final StorageItemRepository storageItemRepository;
    private final StorageItemConverter storageItemConverter;

    public StorageItemServiceImpl(StorageItemRepository storageItemRepository,
            StorageItemConverter storageItemConverter) {
        this.storageItemRepository = storageItemRepository;
        this.storageItemConverter = storageItemConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponseDTO getStorageItems() {

        StorageItemResponseDTO rootStorageItem = StorageItemResponseDTO.builder()
                .name(ROOT_FOLDER_NAME)
                .itemType(ItemType.FOLDER)
                .build();

        List<StorageItemResponseDTO> content = storageItemRepository.findAllByParentItemIsNull()
                .stream()
                .map(storageItemConverter::toResponseDTO)
                .collect(Collectors.toList());

        return FolderResponseDTO.builder()
                .parent(rootStorageItem)
                .content(content)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponseDTO getStorageItems(UUID parentId) {
        StorageItem rootStorageItem = storageItemRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException()); // TODO exceptions

        StorageItemResponseDTO parent = storageItemConverter.toResponseDTO(rootStorageItem);

        List<StorageItemResponseDTO> content = rootStorageItem.getContents()
                .stream()
                .map(storageItemConverter::toResponseDTO)
                .collect(Collectors.toList());

        return FolderResponseDTO.builder()
                .parent(parent)
                .content(content)
                .build();
    }

    @Override
    @Transactional
    public StorageItemResponseDTO updateStorageItem(UUID storageItemId, StorageItemRequestDTO request) {
        
        StorageItem storageItemToUpdate = storageItemRepository.findById(storageItemId)
                .orElseThrow(() -> new RuntimeException());

        storageItemToUpdate.setName(request.getName() != null ? request.getName() : storageItemToUpdate.getName());
        
        StorageItem parentStorageItem = null; //parent nullable, for moving to the root folder

        if (request.getParentId() != null) {
            parentStorageItem = storageItemRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException());
        }

        storageItemToUpdate.setParentItem(parentStorageItem);

        return storageItemConverter.toResponseDTO(storageItemRepository.save(storageItemToUpdate));
    }

    @Override
    @Transactional
    public void deleteStorageItem(UUID storageItemId) {
        storageItemRepository.deleteById(storageItemId);
    }
}
