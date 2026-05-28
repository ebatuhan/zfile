package com.batu.zfile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batu.zfile.entity.StorageItem;

public interface StorageItemRepository extends JpaRepository<StorageItem, UUID> {
    List<StorageItem> findAllByParentItem_StorageItemId(UUID parentItemId);
    List<StorageItem> findAllByParentItemIsNull();
}
