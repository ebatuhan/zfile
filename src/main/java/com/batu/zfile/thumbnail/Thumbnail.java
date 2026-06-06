package com.batu.zfile.thumbnail;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.batu.zfile.metadata.FileMetadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;

import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_thumbnail_file_metadata_id", columnList = "file_metadata_id", unique = true),
        @Index(name = "idx_thumbnail_status", columnList = "status")
})
public class Thumbnail {

    @Id
    @UuidGenerator
    private UUID thumbnailId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FileMetadata metadata;

    private String objectKey;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
