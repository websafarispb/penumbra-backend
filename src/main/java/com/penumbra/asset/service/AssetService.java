package com.penumbra.asset.service;

import com.penumbra.asset.dto.AssetDownloadResponse;
import com.penumbra.asset.dto.AssetResponse;
import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.mapper.AssetMapper;
import com.penumbra.asset.model.Asset;
import com.penumbra.asset.repository.AssetRepository;
import com.penumbra.exception.NotFoundException;
import com.penumbra.storage.StorageService;
import com.penumbra.user.model.User;
import com.penumbra.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

import static com.penumbra.utils.Constants.ASSET_WITH_ID_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final StorageService storageService;
    private final UserService userService;

    public AssetResponse create(CreateAssetRequest request) {
        Asset asset = assetMapper.toEntity(request);

        User owner = userService.findById(request.getOwnerId());
        asset.setOwner(owner);

        Asset savedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(savedAsset);
    }

    public List<AssetResponse> findAll() {
        return assetRepository.findAllWithOwner()
                .stream()
                .map(assetMapper::toResponse)
                .toList();
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Asset not found with id: " + id));

        return assetMapper.toResponse(asset);
    }

    public AssetResponse update(Long id, UpdateAssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ASSET_WITH_ID_NOT_FOUND_MESSAGE.formatted(id)));

        assetMapper.updateEntity(asset, request);

        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(updatedAsset);
    }

    public void deleteById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ASSET_WITH_ID_NOT_FOUND_MESSAGE.formatted(id)));

        assetRepository.delete(asset);
    }

    public AssetResponse uploadFile(Long id, MultipartFile file) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset not found with id: " + id));

        String storageKey = storageService.uploadFile(file);

        asset.setStorageKey(storageKey);
        asset.setOriginalFileName(file.getOriginalFilename());
        asset.setMimeType(file.getContentType());
        asset.setFileSize(file.getSize());

        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponse(updatedAsset);
    }

    public AssetDownloadResponse downloadFile(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset not found with id: " + id));

        if (asset.getStorageKey() == null || asset.getStorageKey().isBlank()) {
            throw new NotFoundException("File is not uploaded for asset with id: " + id);
        }

        InputStream inputStream = storageService.downloadFile(asset.getStorageKey());

        String fileName = asset.getOriginalFileName() != null
                ? asset.getOriginalFileName()
                : "file";

        String contentType = asset.getMimeType() != null
                ? asset.getMimeType()
                : "application/octet-stream";

        return AssetDownloadResponse.builder()
                .inputStream(inputStream)
                .fileName(fileName)
                .contentType(contentType)
                .build();
    }

    public List<AssetResponse> findAllByOwnerId(Long ownerId) {
        return assetRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(assetMapper::toResponse)
                .toList();
    }

    public Page<AssetResponse> findAll(Long ownerId, Pageable pageable) {
        Page<Asset> page = ownerId != null
                ? assetRepository.findAllByOwnerId(ownerId, pageable)
                : assetRepository.findAll(pageable);

        return page.map(assetMapper::toResponse);
    }
}