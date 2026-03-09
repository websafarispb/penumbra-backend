package com.penumbra.user.model;

import com.penumbra.asset.model.Asset;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private Instant createdAt;

    @OneToMany(mappedBy = "owner")
    private List<Asset> assets;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}