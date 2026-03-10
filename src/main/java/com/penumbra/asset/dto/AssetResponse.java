package com.penumbra.asset.dto;

import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AssetResponse {

    private Long id;
    private String title;
    private String description;
    private AssetType assetType;
    private AssetVisibility visibility;
    private AssetStatus status;
    private String storageKey;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private Instant createdAt;
    private Instant updatedAt;
    private Long ownerId;
    private String ownerUsername;
}