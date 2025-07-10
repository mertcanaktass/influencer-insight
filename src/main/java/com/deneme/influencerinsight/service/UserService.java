package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.model.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getAllUsers();

    boolean existsByUsername(String username);

    void saveUser(UserEntity userEntity);

    void registerAdminUser(RegisterRequest request) throws Exception;

    void register(RegisterRequest registerRequest) throws Exception;
}
