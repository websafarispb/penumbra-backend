package com.penumbra.asset.service;

import com.penumbra.IntegrationTestBase;
import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import com.penumbra.asset.repository.AssetRepository;
import com.penumbra.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetServiceIT extends IntegrationTestBase {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetRepository assetRepository;

    @AfterEach
    void cleanUp() {
        assetRepository.deleteAll();
    }

    @Test
    void create_shouldSaveAssetToRealDatabase() {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setTitle("Sunset in Izmir");
        request.setDescription("Evening light over the sea");
        request.setAssetType(AssetType.PHOTO);
        request.setVisibility(AssetVisibility.PRIVATE);
        request.setStatus(AssetStatus.DRAFT);
        request.setStorageKey("assets/sunset.jpg");
        request.setOriginalFileName("sunset.jpg");
        request.setMimeType("image/jpeg");
        request.setFileSize(12345L);

        var response = assetService.create(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Sunset in Izmir");
        assertThat(response.getAssetType()).isEqualTo(AssetType.PHOTO);
        assertThat(assetRepository.findById(response.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnSavedAsset() {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setTitle("Test asset");
        request.setDescription("Service integration test");
        request.setAssetType(AssetType.PHOTO);
        request.setVisibility(AssetVisibility.PUBLIC);
        request.setStatus(AssetStatus.ACTIVE);

        var created = assetService.create(request);

        var found = assetService.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("Test asset");
        assertThat(found.getVisibility()).isEqualTo(AssetVisibility.PUBLIC);
    }

    @Test
    void update_shouldUpdateExistingAsset() {
        CreateAssetRequest createRequest = new CreateAssetRequest();
        createRequest.setTitle("Old title");
        createRequest.setDescription("Old description");
        createRequest.setAssetType(AssetType.PHOTO);
        createRequest.setVisibility(AssetVisibility.PRIVATE);
        createRequest.setStatus(AssetStatus.DRAFT);

        var created = assetService.create(createRequest);

        UpdateAssetRequest updateRequest = new UpdateAssetRequest();
        updateRequest.setTitle("Updated title");
        updateRequest.setDescription("Updated description");
        updateRequest.setAssetType(AssetType.ILLUSTRATION);
        updateRequest.setVisibility(AssetVisibility.PUBLIC);
        updateRequest.setStatus(AssetStatus.ACTIVE);
        updateRequest.setStorageKey("assets/updated.png");
        updateRequest.setOriginalFileName("updated.png");
        updateRequest.setMimeType("image/png");
        updateRequest.setFileSize(999L);

        var updated = assetService.update(created.getId(), updateRequest);

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getAssetType()).isEqualTo(AssetType.ILLUSTRATION);
        assertThat(updated.getVisibility()).isEqualTo(AssetVisibility.PUBLIC);
        assertThat(updated.getStatus()).isEqualTo(AssetStatus.ACTIVE);
        assertThat(updated.getMimeType()).isEqualTo("image/png");
    }

    @Test
    void delete_shouldRemoveAssetFromRealDatabase() {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setTitle("To delete");
        request.setAssetType(AssetType.PHOTO);
        request.setVisibility(AssetVisibility.PRIVATE);
        request.setStatus(AssetStatus.DRAFT);

        var created = assetService.create(request);

        assetService.deleteById(created.getId());

        assertThat(assetRepository.findById(created.getId())).isEmpty();
    }

    @Test
    void findById_shouldThrowNotFoundException_whenAssetDoesNotExist() {
        assertThatThrownBy(() -> assetService.findById(999999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Asset not found with id: 999999");
    }
}