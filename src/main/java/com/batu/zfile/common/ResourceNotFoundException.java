package com.batu.zfile.common;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException{

    public ResourceNotFoundException(@Nullable String reason) {
        super(HttpStatus.NOT_FOUND,
            reason == null || reason.isBlank()
            ? "Resource not found."
            : reason 
        );
    }

}
