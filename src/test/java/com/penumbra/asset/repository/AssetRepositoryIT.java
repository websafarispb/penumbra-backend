package com.penumbra.asset.repository;

import com.penumbra.IntegrationTestBase;
import com.penumbra.asset.model.Asset;
import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AssetRepositoryIT extends IntegrationTestBase {

    @Autowired
    private AssetRepository assetRepository;

    @AfterEach
    void cleanUp() {
        assetRepository.deleteAll();
    }

    @Test
    void saveAndFindById_shouldWorkWithRealDatabase() {
        Asset asset = new Asset();
        asset.setTitle("Test asset");
        asset.setDescription("Repository integration test");
        asset.setAssetType(AssetType.PHOTO);
        asset.setVisibility(AssetVisibility.PRIVATE);
        asset.setStatus(AssetStatus.DRAFT);
        asset.setStorageKey("assets/test.jpg");
        asset.setOriginalFileName("test.jpg");
        asset.setMimeType("image/jpeg");
        asset.setFileSize(12345L);

        Asset saved = assetRepository.save(asset);

        Optional<Asset> found = assetRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test asset");
        assertThat(found.get().getAssetType()).isEqualTo(AssetType.PHOTO);
        assertThat(found.get().getMimeType()).isEqualTo("image/jpeg");
    }
}