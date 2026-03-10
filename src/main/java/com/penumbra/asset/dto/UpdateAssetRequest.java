package com.penumbra.asset.dto;

import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAssetRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private AssetType assetType;

    @NotNull
    private AssetVisibility visibility;

    @NotNull
    private AssetStatus status;

    private String storageKey;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
}