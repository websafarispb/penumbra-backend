package com.penumbra.asset.controller;

import com.penumbra.asset.dto.AssetDownloadResponse;
import com.penumbra.asset.dto.AssetResponse;
import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.model.Asset;
import com.penumbra.asset.repository.AssetRepository;
import com.penumbra.asset.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final AssetRepository assetRepository;

    @PostMapping
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request) {
        return assetService.create(request);
    }

    @GetMapping
    public List<AssetResponse> findAll() {
        return assetService.findAll();
    }

    @GetMapping("/{id}")
    public AssetResponse findById(@PathVariable Long id) {
        return assetService.findById(id);
    }

    @PutMapping("/{id}")
    public AssetResponse update(@PathVariable Long id,
                                @Valid @RequestBody UpdateAssetRequest request) {
        return assetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        assetService.deleteById(id);
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssetResponse uploadFile(@PathVariable Long id,
                                    @RequestParam("file") MultipartFile file) {
        return assetService.uploadFile(id, file);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
        AssetDownloadResponse response = assetService.downloadFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .body(new InputStreamResource(response.getInputStream()));
    }

    @GetMapping("/by-owner/{ownerId}")
    public List<AssetResponse> findAllByOwnerId(@PathVariable Long ownerId) {
        return assetService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/paged")
    public Page<AssetResponse> findAllPaged(
            @RequestParam(required = false) Long ownerId,
            Pageable pageable
    ) {
        return assetService.findAll(ownerId, pageable);
    }
}