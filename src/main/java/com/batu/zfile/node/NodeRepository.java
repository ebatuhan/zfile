package com.batu.zfile.node;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, UUID> {

}
