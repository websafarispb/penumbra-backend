package com.penumbra.user.controller;

import com.penumbra.asset.controller.AssetController;
import com.penumbra.asset.dto.AssetResponse;
import com.penumbra.user.dto.CreateUserRequest;
import com.penumbra.user.dto.UpdateUserRequest;
import com.penumbra.user.dto.UserResponse;
import com.penumbra.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AssetController assetService;

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findResponseById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/{id}/assets")
    public List<AssetResponse> findAssetsByUserId(@PathVariable Long id) {
        userService.findById(id);
        return assetService.findAllByOwnerId(id);
    }
}