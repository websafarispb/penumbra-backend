package com.penumbra.user.service;

import com.penumbra.exception.NotFoundException;
import com.penumbra.user.dto.CreateUserRequest;
import com.penumbra.user.dto.UpdateUserRequest;
import com.penumbra.user.dto.UserResponse;
import com.penumbra.user.model.User;
import com.penumbra.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(CreateUserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();

        user = userRepository.save(user);
        return toResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findResponseById(Long id) {
        return toResponse(findById(id));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findById(id);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user = userRepository.save(user);
        return toResponse(user);
    }

    public void deleteById(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}