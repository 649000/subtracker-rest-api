package com0.subtracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_entity")
@EntityListeners(AuditingEntityListener.class)
@JsonApiResource(type = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonApiId
    private UUID userID;

    @JsonProperty
    @Column(nullable = false)
    private String name;

    @NotBlank
    @NotNull
    @NotEmpty
    @Email
    @Column(nullable = false)
    private String email;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date modifiedDate;
}
