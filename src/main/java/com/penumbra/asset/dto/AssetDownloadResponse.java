package com.penumbra.asset.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class AssetDownloadResponse {

    private InputStream inputStream;
    private String fileName;
    private String contentType;
}