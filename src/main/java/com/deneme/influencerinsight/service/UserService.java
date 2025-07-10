package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.model.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getAllUsers();

    void registerAdminUser(RegisterRequest request) throws Exception;
}
