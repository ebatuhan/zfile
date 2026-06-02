package com.batu.zfile.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.batu.zfile.service.ThumbnailService;

@Component
public class FileUploadedListener {

    private final ThumbnailService thumbnailService;

    public FileUploadedListener(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(FileUploaded event) {
        thumbnailService.generateForMetadata(event.fileMetadataId());
    }
}
