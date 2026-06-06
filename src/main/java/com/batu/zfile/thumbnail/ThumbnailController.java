package com.batu.zfile.thumbnail;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batu.zfile.thumbnail.dto.ThumbnailDownloadDTO;

@RestController
@RequestMapping("/thumbnails")
public class ThumbnailController {
    private final ThumbnailService thumbnailService;

    public ThumbnailController(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @GetMapping("/{thumbnailId}")
    public ResponseEntity<ThumbnailDownloadDTO> downloadThumbnail(UUID thumbnailId){
        return ResponseEntity.ok(thumbnailService.downloadThumbnail(thumbnailId));
    }
    
}
