package com.penumbra.asset.repository;

import com.penumbra.asset.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("select a from Asset a left join fetch a.owner")
    List<Asset> findAllWithOwner();

    @Query("select a from Asset a left join fetch a.owner where a.id = :id")
    Optional<Asset> findByIdWithOwner(Long id);

    @EntityGraph(attributePaths = "owner")
    List<Asset> findAll();

    @EntityGraph(attributePaths = "owner")
    Optional<Asset> findById(Long id);

    @EntityGraph(attributePaths = "owner")
    List<Asset> findAllByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = "owner")
    Page<Asset> findAllByOwnerId(Long ownerId, Pageable pageable);
}