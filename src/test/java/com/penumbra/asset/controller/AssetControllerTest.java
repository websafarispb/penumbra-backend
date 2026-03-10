package com.penumbra.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.penumbra.asset.dto.AssetDownloadResponse;
import com.penumbra.asset.dto.AssetResponse;
import com.penumbra.asset.dto.CreateAssetRequest;
import com.penumbra.asset.dto.UpdateAssetRequest;
import com.penumbra.asset.model.AssetStatus;
import com.penumbra.asset.model.AssetType;
import com.penumbra.asset.model.AssetVisibility;
import com.penumbra.asset.repository.AssetRepository;
import com.penumbra.asset.service.AssetService;
import com.penumbra.config.SecurityConfig;
import com.penumbra.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.penumbra.utils.Constants.ASSET_WITH_ID_NOT_FOUND_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetController.class)
@Import(SecurityConfig.class)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssetService assetService;

    @MockBean
    private AssetRepository assetRepository;

    @Test
    void create_shouldReturnCreatedAsset() throws Exception {
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

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .title("Sunset in Izmir")
                .description("Evening light over the sea")
                .assetType(AssetType.PHOTO)
                .visibility(AssetVisibility.PRIVATE)
                .status(AssetStatus.DRAFT)
                .storageKey("assets/2026/03/sunset.jpg")
                .originalFileName("sunset.jpg")
                .mimeType("image/jpeg")
                .fileSize(2457600L)
                .build();

        when(assetService.create(any(CreateAssetRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sunset in Izmir"))
                .andExpect(jsonPath("$.assetType").value("PHOTO"));
    }

    @Test
    void findAll_shouldReturnAssets() throws Exception {
        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .title("Sunset")
                .assetType(AssetType.PHOTO)
                .visibility(AssetVisibility.PUBLIC)
                .status(AssetStatus.ACTIVE)
                .build();

        when(assetService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Sunset"));
    }

    @Test
    void findById_shouldReturnAsset() throws Exception {
        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .title("Sunset")
                .assetType(AssetType.PHOTO)
                .visibility(AssetVisibility.PUBLIC)
                .status(AssetStatus.ACTIVE)
                .build();

        when(assetService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/assets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sunset"));
    }

    @Test
    void findById_shouldReturn404_whenAssetNotFound() throws Exception {

        when(assetService.findById(1L))
                .thenThrow(new NotFoundException(ASSET_WITH_ID_NOT_FOUND_MESSAGE.formatted("1")));

        mockMvc.perform(get("/api/assets/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void update_shouldReturnUpdatedAsset() throws Exception {
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

        AssetResponse response = AssetResponse.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .assetType(AssetType.PHOTO)
                .visibility(AssetVisibility.PUBLIC)
                .status(AssetStatus.ACTIVE)
                .storageKey("assets/updated.jpg")
                .originalFileName("updated.jpg")
                .mimeType("image/jpeg")
                .fileSize(1000L)
                .build();

        when(assetService.update(org.mockito.ArgumentMatchers.eq(1L), any(UpdateAssetRequest.class)))
                .thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void deleteById_shouldReturnNoContent() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/assets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void downloadFile_shouldReturnAttachment() throws Exception {
        AssetDownloadResponse response = AssetDownloadResponse.builder()
                .inputStream(new java.io.ByteArrayInputStream("test".getBytes()))
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .build();

        when(assetService.downloadFile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/assets/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.jpg\""))
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void create_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        String invalidJson = """
            {
              "title": "",
              "description": "Invalid asset",
              "assetType": null,
              "visibility": null,
              "status": null
            }
            """;

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/assets"));
    }
}