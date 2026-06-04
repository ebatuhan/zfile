package com.batu.zfile.common;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> items,
        String nextCursor,
        boolean hasNext) {
}
