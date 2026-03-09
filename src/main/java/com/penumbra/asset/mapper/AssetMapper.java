package com.penumbra.asset.mapper;

import com.penumbra.asset.dto.AssetResponse;
import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.model.Asset;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public Asset toEntity(CreateAssetRequest request) {
        Asset asset = new Asset();
        asset.setTitle(request.getTitle());
        asset.setDescription(request.getDescription());
        asset.setAssetType(request.getAssetType());
        asset.setVisibility(request.getVisibility());
        asset.setStatus(request.getStatus());
        asset.setStorageKey(request.getStorageKey());
        asset.setOriginalFileName(request.getOriginalFileName());
        asset.setMimeType(request.getMimeType());
        asset.setFileSize(request.getFileSize());
        return asset;
    }

    public void updateEntity(Asset asset, UpdateAssetRequest request) {
        asset.setTitle(request.getTitle());
        asset.setDescription(request.getDescription());
        asset.setAssetType(request.getAssetType());
        asset.setVisibility(request.getVisibility());
        asset.setStatus(request.getStatus());
        asset.setStorageKey(request.getStorageKey());
        asset.setOriginalFileName(request.getOriginalFileName());
        asset.setMimeType(request.getMimeType());
        asset.setFileSize(request.getFileSize());
    }

    public AssetResponse toResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .title(asset.getTitle())
                .description(asset.getDescription())
                .assetType(asset.getAssetType())
                .visibility(asset.getVisibility())
                .status(asset.getStatus())
                .storageKey(asset.getStorageKey())
                .originalFileName(asset.getOriginalFileName())
                .mimeType(asset.getMimeType())
                .fileSize(asset.getFileSize())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .ownerId(asset.getOwner() != null ? asset.getOwner().getId() : null)
                .ownerUsername(asset.getOwner() != null ? asset.getOwner().getUsername() : null)
                .build();
    }
}