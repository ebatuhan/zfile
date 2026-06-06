package com.batu.zfile.thumbnail.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.batu.zfile.node.event.NodeCreatedEvent;
import com.batu.zfile.thumbnail.ThumbnailService;

@Component
public class ThumbnailGenerationListener {

    private final ThumbnailService thumbnailService;

    public ThumbnailGenerationListener(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFileNodeCreated(NodeCreatedEvent event) {
        thumbnailService.generateThumbnailForFileAndSave(event.nodeId());
    }
}