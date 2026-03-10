package com.penumbra.asset.service;

import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.mapper.AssetMapper;
import com.penumbra.asset.model.Asset;
import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import com.penumbra.asset.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Spy
    private AssetMapper assetMapper = new AssetMapper();

    @InjectMocks
    private AssetService assetService;

    @Test
    void create_shouldSaveAndReturnAssetResponse() {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setTitle("Sunset in Izmir");
        request.setDescription("Evening light over the sea");
        request.setAssetType(AssetType.PHOTO);
        request.setVisibility(AssetVisibility.PRIVATE);
        request.setStatus(AssetStatus.DRAFT);
        request.setStorageKey("assets/2026/03/sunset.jpg");
        request.setOriginalFileName("sunset.jpg");
        request.setMimeType("image/jpeg");
        request.setFileSize(2457600L);

        Asset savedAsset = new Asset();
        savedAsset.setId(1L);
        savedAsset.setTitle(request.getTitle());
        savedAsset.setDescription(request.getDescription());
        savedAsset.setAssetType(request.getAssetType());
        savedAsset.setVisibility(request.getVisibility());
        savedAsset.setStatus(request.getStatus());
        savedAsset.setStorageKey(request.getStorageKey());
        savedAsset.setOriginalFileName(request.getOriginalFileName());
        savedAsset.setMimeType(request.getMimeType());
        savedAsset.setFileSize(request.getFileSize());

        when(assetRepository.save(any(Asset.class))).thenReturn(savedAsset);

        var response = assetService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Sunset in Izmir");
        assertThat(response.getAssetType()).isEqualTo(AssetType.PHOTO);
        assertThat(response.getVisibility()).isEqualTo(AssetVisibility.PRIVATE);
        assertThat(response.getStatus()).isEqualTo(AssetStatus.DRAFT);
    }

    @Test
    void findAll_shouldReturnAllAssets() {
        Asset asset1 = new Asset();
        asset1.setId(1L);
        asset1.setTitle("Asset 1");
        asset1.setAssetType(AssetType.PHOTO);
        asset1.setVisibility(AssetVisibility.PUBLIC);
        asset1.setStatus(AssetStatus.ACTIVE);

        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setTitle("Asset 2");
        asset2.setAssetType(AssetType.DOCUMENT);
        asset2.setVisibility(AssetVisibility.PRIVATE);
        asset2.setStatus(AssetStatus.DRAFT);

        when(assetRepository.findAll()).thenReturn(List.of(asset1, asset2));

        var result = assetService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Asset 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Asset 2");
    }

    @Test
    void findById_shouldReturnAssetWhenExists() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setTitle("Sunset");
        asset.setAssetType(AssetType.PHOTO);
        asset.setVisibility(AssetVisibility.PUBLIC);
        asset.setStatus(AssetStatus.ACTIVE);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        var result = assetService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Sunset");
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Asset not found with id: 99");
    }

    @Test
    void update_shouldUpdateAndReturnAssetResponse() {
        UpdateAssetRequest request = new UpdateAssetRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        request.setAssetType(AssetType.PHOTO);
        request.setVisibility(AssetVisibility.PUBLIC);
        request.setStatus(AssetStatus.ACTIVE);
        request.setStorageKey("assets/updated.jpg");
        request.setOriginalFileName("updated.jpg");
        request.setMimeType("image/jpeg");
        request.setFileSize(1000L);

        Asset existingAsset = new Asset();
        existingAsset.setId(1L);
        existingAsset.setTitle("Old title");
        existingAsset.setAssetType(AssetType.PHOTO);
        existingAsset.setVisibility(AssetVisibility.PRIVATE);
        existingAsset.setStatus(AssetStatus.DRAFT);

        Asset savedAsset = new Asset();
        savedAsset.setId(1L);
        savedAsset.setTitle("Updated title");
        savedAsset.setDescription("Updated description");
        savedAsset.setAssetType(AssetType.PHOTO);
        savedAsset.setVisibility(AssetVisibility.PUBLIC);
        savedAsset.setStatus(AssetStatus.ACTIVE);
        savedAsset.setStorageKey("assets/updated.jpg");
        savedAsset.setOriginalFileName("updated.jpg");
        savedAsset.setMimeType("image/jpeg");
        savedAsset.setFileSize(1000L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(existingAsset));
        when(assetRepository.save(existingAsset)).thenReturn(savedAsset);

        var result = assetService.update(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Updated title");
        assertThat(result.getVisibility()).isEqualTo(AssetVisibility.PUBLIC);
        assertThat(result.getStatus()).isEqualTo(AssetStatus.ACTIVE);
    }

    @Test
    void deleteById_shouldDeleteAssetWhenExists() {
        Asset asset = new Asset();
        asset.setId(1L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assetService.deleteById(1L);

        org.mockito.Mockito.verify(assetRepository).delete(asset);
    }
}