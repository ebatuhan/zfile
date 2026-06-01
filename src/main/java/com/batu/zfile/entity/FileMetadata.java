package com.batu.zfile.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
        @Index(name = "idx_file_metadata_file_node_id", columnList = "file_node_id", unique = true),
        @Index(name = "idx_file_metadata_object_key", columnList = "object_key", unique = true)
})
public class FileMetadata {

    @Id
    @UuidGenerator
    private UUID fileMetadataId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_node_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FileNode node;

    @Column(nullable = false)
    private String objectKey;

    @Column(nullable = false)
    private Long size;

    private String contentType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
