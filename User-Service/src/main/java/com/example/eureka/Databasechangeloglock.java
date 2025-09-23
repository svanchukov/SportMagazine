package com.example.eureka;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "databasechangeloglock", schema = "public")
public class Databasechangeloglock {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Column(name = "lockgranted")
    private Instant lockgranted;

    @Size(max = 255)
    @Column(name = "lockedby")
    private String lockedby;

}