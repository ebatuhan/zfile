package com.batu.zfile.node.dto;

import java.util.UUID;

public record NodeRequestDTO(
    String name,
    UUID parentId
) {

}
