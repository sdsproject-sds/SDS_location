package org.sds.sdslocation.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Timestamp;

/**
 * Abstract base class for auditable entities.
 * Provides common auditing fields for tracking creation and modification details.
 * 
 * @author joseph.kibe
 * Created on 5/3/2026
 */
@Getter
@Setter
public abstract class AuditableEntity {

    @Column("status")
    private Status status = Status.ACTIVE;
    
    @CreatedDate
    @Column("created_at")
    private Timestamp createdAt;
    
    @LastModifiedDate
    @Column("updated_at")
    private Timestamp updatedAt;
    
    @CreatedBy
    @Column("created_by")
    private String createdBy;
    
    @LastModifiedBy
    @Column("updated_by")
    private String updatedBy;
}