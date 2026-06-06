package com.batu.zfile.metadata;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.batu.zfile.node.Node;
import com.batu.zfile.thumbnail.Thumbnail;

import jakarta.persistence.CascadeType;
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
        @Index(name = "idx_file_metadata_node_id", columnList = "node_id", unique = true),
        @Index(name = "idx_file_metadata_object_key", columnList = "object_key", unique = true)
})
public class FileMetadata {

    @Id
    @UuidGenerator
    private UUID fileMetadataId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Node node;

    @Column(nullable = false)
    private String objectKey;

    @Column(nullable = false)
    private long size;

    private String contentType;

    @OneToOne(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Thumbnail thumbnail;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
