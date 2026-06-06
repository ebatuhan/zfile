package com.batu.zfile.thumbnail.factory;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.batu.zfile.thumbnail.strategy.ThumbnailGeneratorStrategy;

@Component
public class ThumbnailGeneratorFactory {

    private final Map<String, ThumbnailGeneratorStrategy> strategies;

    public ThumbnailGeneratorFactory(Map<String, ThumbnailGeneratorStrategy> strategies) {
        this.strategies = strategies;
    }

    public Optional<ThumbnailGeneratorStrategy> getStrategy(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return Optional.empty();
        }

        if (contentType.startsWith("image/")) {
            return Optional.ofNullable(strategies.get("image"));
        }

        return Optional.ofNullable(strategies.get(contentType));
    }
}
