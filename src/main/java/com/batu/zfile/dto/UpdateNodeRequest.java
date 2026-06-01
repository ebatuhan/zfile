package com.batu.zfile.dto;

import java.util.UUID;

public record UpdateNodeRequest(String name, UUID parentId) {
}
