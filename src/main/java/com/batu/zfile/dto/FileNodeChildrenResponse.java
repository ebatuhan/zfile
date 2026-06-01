package com.batu.zfile.dto;

import java.util.List;

public record FileNodeChildrenResponse(FileNodeResponse current, List<PathSegmentResponse> path, List<FileNodeResponse> children) {
}
