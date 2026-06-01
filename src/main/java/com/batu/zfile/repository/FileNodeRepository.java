package com.batu.zfile.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.batu.zfile.entity.FileNode;

public interface FileNodeRepository extends JpaRepository<FileNode, UUID> {

    @EntityGraph(attributePaths = "metadata")
    List<FileNode> findByParentIsNullOrderByTypeAscNameAsc();

    @EntityGraph(attributePaths = "metadata")
    List<FileNode> findByParent_FileNodeIdOrderByTypeAscNameAsc(UUID parentId);

    @EntityGraph(attributePaths = "metadata")
    @Query("select node from FileNode node where node.fileNodeId = :id")
    Optional<FileNode> findWithMetadataById(@Param("id") UUID id);

    @Query(value = """
            with recursive path as (
                select file_node_id, name, parent_id, 0 as depth
                from file_node
                where file_node_id = :id

                union all

                select parent.file_node_id, parent.name, parent.parent_id, child.depth + 1
                from file_node parent
                join path child on child.parent_id = parent.file_node_id
            )
            select file_node_id as id, name
            from path
            order by depth desc
            """, nativeQuery = true)
    List<PathSegmentProjection> findPathById(@Param("id") UUID id);

    @Query(value = """
            with recursive ancestors as (
                select file_node_id, parent_id
                from file_node
                where file_node_id = :startNodeId

                union

                select parent.file_node_id, parent.parent_id
                from file_node parent
                join ancestors child on child.parent_id = parent.file_node_id
            )
            select exists (
                select 1
                from ancestors
                where file_node_id = :candidateAncestorId
            )
            """, nativeQuery = true)
    boolean existsByFileNodeIdInAncestorChain(
            @Param("startNodeId") UUID startNodeId,
            @Param("candidateAncestorId") UUID candidateAncestorId);

    interface PathSegmentProjection {
        UUID getId();

        String getName();
    }
}
