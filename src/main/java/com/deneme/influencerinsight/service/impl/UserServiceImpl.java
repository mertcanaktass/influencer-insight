package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.User;
import com.deneme.influencerinsight.repository.UserRepository;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
