package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.Influencer;
import com.deneme.influencerinsight.repository.InfluencerRepository;
import com.deneme.influencerinsight.service.InfluencerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfluencerServiceImpl implements InfluencerService {

    private final InfluencerRepository repository;

    public InfluencerServiceImpl(InfluencerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Influencer save(Influencer influencer) {
        return repository.save(influencer);
    }

    @Override
    public List<Influencer> getAll() {
        return repository.findAll();
    }
}
