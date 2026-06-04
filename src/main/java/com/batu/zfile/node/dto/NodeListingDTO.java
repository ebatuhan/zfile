package com.batu.zfile.node.dto;

import java.util.List;

public record NodeListingDTO(
    NodeItemDTO current,
    List<NodeItemDTO> content
) {

}
