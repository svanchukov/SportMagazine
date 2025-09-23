package com.example.eureka;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "databasechangelog", schema = "public")
public class Databasechangelog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Size(max = 255)
    @NotNull
    @Column(name = "id", nullable = false)
    private String id;

    @Size(max = 255)
    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @Size(max = 255)
    @NotNull
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Column(name = "dateexecuted", nullable = false)
    private Instant dateexecuted;

    @NotNull
    @Column(name = "orderexecuted", nullable = false)
    private Integer orderexecuted;

    @Size(max = 10)
    @NotNull
    @Column(name = "exectype", nullable = false, length = 10)
    private String exectype;

    @Size(max = 35)
    @Column(name = "md5sum", length = 35)
    private String md5sum;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "comments")
    private String comments;

    @Size(max = 255)
    @Column(name = "tag")
    private String tag;

    @Size(max = 20)
    @Column(name = "liquibase", length = 20)
    private String liquibase;

    @Size(max = 255)
    @Column(name = "contexts")
    private String contexts;

    @Size(max = 255)
    @Column(name = "labels")
    private String labels;

    @Size(max = 10)
    @Column(name = "deployment_id", length = 10)
    private String deploymentId;

}