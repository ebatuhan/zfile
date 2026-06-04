package com.batu.zfile.node;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.KeysetScrollPosition;

import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodeRepository extends JpaRepository<Node, UUID> {
    Window<Node> findFirst20ByParentIdOrderByCreatedAtDescNodeIdDesc(UUID parentId, KeysetScrollPosition position);

    @Query(value = """
            with recursive path as (
                select node_id, name, parent_id, 0 as depth
                from file_node
                where node_id = :id

                union all

                select parent.node_id, parent.name, parent.parent_id, child.depth + 1
                from file_node parent
                join path child on child.parent_id = parent.node_id
                where child.depth < :maxDepth
            )
            select node_id as id, name
            from path
            order by depth desc
            """, nativeQuery = true)
    List<PathSegmentProjection> findPathByNodeId(
            @Param("id") UUID id,
            @Param("maxDepth") int maxDepth);
}
