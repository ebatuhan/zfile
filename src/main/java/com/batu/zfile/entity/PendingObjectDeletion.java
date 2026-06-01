package com.batu.zfile.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(indexes = @Index(name = "idx_pending_object_deletion_created_at", columnList = "created_at"))
public class PendingObjectDeletion {

    @Id
    @UuidGenerator
    private UUID pendingObjectDeletionId;

    @Column(nullable = false)
    private String objectKey;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
