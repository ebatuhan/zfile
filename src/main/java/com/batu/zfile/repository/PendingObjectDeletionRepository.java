package com.batu.zfile.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.batu.zfile.entity.PendingObjectDeletion;

public interface PendingObjectDeletionRepository extends JpaRepository<PendingObjectDeletion, UUID> {

    List<PendingObjectDeletion> findTop100ByOrderByCreatedAtAsc();

    @Modifying
    @Query(value = """
            with recursive subtree as (
                select file_node_id
                from file_node
                where file_node_id = :nodeId

                union all

                select child.file_node_id
                from file_node child
                join subtree parent on child.parent_id = parent.file_node_id
            )
            insert into pending_object_deletion (pending_object_deletion_id, object_key, created_at)
            select metadata.file_metadata_id, metadata.object_key, now()
            from file_metadata metadata
            where metadata.file_node_id in (select file_node_id from subtree)
            """, nativeQuery = true)
    int enqueueObjectsForDeletedNode(@Param("nodeId") UUID nodeId);
}
